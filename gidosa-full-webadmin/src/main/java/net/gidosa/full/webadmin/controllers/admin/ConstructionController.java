package net.gidosa.full.webadmin.controllers.admin;

import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webadmin.services.ConstructionService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/construction")
public class ConstructionController {
    private final ConstructionService constructionService;

    @GetMapping("/list")
    public String list(@PageableDefault(size = 10) Pageable pageable, Model model) {
        Page<Construction> constructions = constructionService.findAllConstructions(pageable);
        model.addAttribute("constructions", constructions);
        return "main/construction/list";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("construction", new Construction());
        return "main/construction/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Construction construction) {
        constructionService.saveConstruction(construction);
        return "redirect:/construction/list";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Construction construction = constructionService.getConstructionWithManagementMenus(id);
        model.addAttribute("construction", construction);
        return "main/construction/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Construction construction) {
        construction.setId(id);
        constructionService.saveConstruction(construction);
        return "redirect:/construction/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        constructionService.deleteConstruction(id);
        return "redirect:/construction/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Construction construction = constructionService.getConstructionWithManagementMenus(id);
        model.addAttribute("construction", construction);
        return "main/construction/detail";
    }
}
