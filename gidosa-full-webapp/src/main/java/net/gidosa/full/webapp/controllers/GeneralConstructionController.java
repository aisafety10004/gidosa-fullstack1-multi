package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general")
public class GeneralConstructionController {
    private final GeneralConstructionService generalConstructionService;

    @GetMapping("/construction/{constructionId}")
    public String index(@PathVariable Long constructionId, Model model) {
        model.addAttribute("constructionId", constructionId);
        model.addAttribute("menuText", "잘한다 건공");

        return "pages/general/construction";
    }
}
