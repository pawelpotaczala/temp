package pl.edu.agh.carhire.controller;

import pl.edu.agh.carhire.model.Hire;
import pl.edu.agh.carhire.service.HireService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Collection;
import java.util.Date;

/**
 * Klasa kontrolujaca kontekst wypozyczen.
 */
@Controller
@RequestMapping("/hires")
public class HiresController {

    private final Logger logger = LoggerFactory.getLogger(HiresController.class);

    @Autowired
    private HireService hireService;

    /**
     * Metoda restowa typu GET pobierajaca wypozyczenia.
     * @param hireDate - data rozpoczecia wypozyczenia.
     * @param model - obiekt przeplywajacy miedzy frontem, a backendem.
     * @return hires.jsp
     */
    @RequestMapping(method=RequestMethod.GET)
    public String getHires(@RequestParam(value="hireDate", required=false) @DateTimeFormat(pattern="yyyy-MM-dd") Date hireDate, Model model) {
        Collection<Hire> hires = (hireDate == null || "".equals(hireDate) ? hireService.findAll() : hireService.findByHireDate(hireDate));

        for(Hire hire: hires) {
            long diff = hire.getReturnDate().getTime() - hire.getHireDate().getTime();
            int days = (int) (diff / (1000*60*60*24));
            hire.setDays(days);
        }
        model.addAttribute("hires", hires);
        return "hires";
    }

    /**
     * Metoda restowa typu GET uzuwajaca wypozyczenie o danym ID
     * @param id wypozyczenia
     * @param redirectAttributes atrybut przekierowania kontekstu
     * @return przekierowanie do /hires
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    public String removeHire(@PathVariable Long id, final RedirectAttributes redirectAttributes) {
        logger.debug("delete hire: {}", id);

        hireService.remove(id);

        redirectAttributes.addFlashAttribute("css", "success");
        redirectAttributes.addFlashAttribute("msg", "Hire is deleted!");

        return "redirect:/hires";
    }
}
