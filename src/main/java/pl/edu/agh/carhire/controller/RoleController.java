package pl.edu.agh.carhire.controller;

import pl.edu.agh.carhire.model.Role;
import pl.edu.agh.carhire.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.HashSet;

/**
 * Klasa kontrolujaca kontekst rol.
 */
@Controller
@Transactional
@RequestMapping("/admin/roles")
public class RoleController {

	private final Logger logger = LoggerFactory.getLogger(RoleController.class);

	@Autowired
	private RoleService roleService;

	@Autowired
	MessageSource messageSource;

	/**
	 * Metoda restowa typu GET pobierajaca role uzytkownikow po nazwie
	 * @param roleName nazwa roli
	 * @param model obiekt przeplywajacy miedzy frontem, a backendem.
	 * @return roles.jsp
	 */
	@RequestMapping(method= RequestMethod.GET)
	public String getRoles(@RequestParam(value="roleName", required=false) String roleName, Model model) {
		Collection<Role> roles;
		if ((roleName == null || "".equals(roleName))) {
			roles = roleService.findAll();
		} else {
			roles = new HashSet<>();
			Role role = roleService.findByName(roleName);
			roles.add(role);
		}
		model.addAttribute("roles", roles);

		return "roles";
	}

	/**
	 * Metoda restowa typu GET inicjalizjaca nowa role
	 * @param model obiekt przeplywajacy miedzy frontem, a backendem.
	 * @return addEditRole.jsp
	 */
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newRole(Model model) {
		model.addAttribute("role", new Role());
		return "addEditRole";
	}

	/**
	 * Metoda restowa typu GET edytujaca dana role
	 * @param id roli
	 * @param model obiekt przeplywajacy miedzy frontem, a backendem.
	 * @return addEditRole.jsp
	 */
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String editRole(@PathVariable Long id, Model model) {
		Role role = roleService.findOne(id);
		model.addAttribute("role", role);
		return "addEditRole";
	}

	/**
	 * Metoda restowa typu POST zapisujaca role do bazy
	 * @param role rola do zapisu
	 * @param result wynik zwrotny
	 * @param model obiekt przeplywajacy miedzy frontem, a backendem.
	 * @param redirectAttributes atrybut przekierowania
	 * @return addEditRole.jsp lub przekierowanie do /admin/roles/{roleID}
	 */
	@RequestMapping(method=RequestMethod.POST)
	public String saveRole(Role role, BindingResult result, Model model, final RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			model.addAttribute("role", role);
			return "addEditRole";
		}

		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "Role saved successfully!");

		roleService.save(role);

		return "redirect:/admin/roles/" + role.getId();
	}

	/**
	 * Metoda restowa typu GET pobierajaca role po ID
	 * @param id roli
	 * @param model obiekt przeplywajacy miedzy frontem, a backendem.
	 * @return showRole.jsp
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String viewRole(@PathVariable("id") Long id, Model model) {

		logger.debug("showRole() id: {}", id);

		Role role = roleService.findOne(id);
		if (role == null) {
			model.addAttribute("css", "danger");
			model.addAttribute("msg", "role not found");
		}
		model.addAttribute("role", role);

		return "showRole";

	}

	/**
	 * Metoda restowa typu GET usuwajaca role po ID
	 * @param id roli
	 * @param redirectAttributes atrybut przekierowania
	 * @return przekierowanie do /roles
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String removeRole(@PathVariable Long id, final RedirectAttributes redirectAttributes) {
		logger.debug("delete role: {}", id);

		roleService.remove(id);

		redirectAttributes.addFlashAttribute("css", "success");
		redirectAttributes.addFlashAttribute("msg", "Role is deleted!");

		return "redirect:/roles";
	}

}
