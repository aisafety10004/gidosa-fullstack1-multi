package net.gidosa.full.webadmin.controllers;

import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webadmin.models.Construction;
import net.gidosa.full.webadmin.repositories.ConstructionRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/construction")
public class ConstructionController {

    private final ConstructionRepository constructionRepository;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("constructions", constructionRepository.findAll());
        return "main/construction/list";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("construction", new Construction());
        return "main/construction/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Construction construction) {
        constructionRepository.save(construction);
        return "redirect:/construction/list";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        Construction construction = constructionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid construction Id:" + id));
        model.addAttribute("construction", construction);
        return "main/construction/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Construction construction) {
        construction.setId(id);
        constructionRepository.save(construction);
        return "redirect:/construction/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        constructionRepository.deleteById(id);
        return "redirect:/construction/list";
    }
}
