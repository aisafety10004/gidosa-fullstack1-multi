package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/auth")
public class GeneralAuthController {
    private final GeneralConstructionService generalConstructionService;

    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
//        model.addAttribute("constructionId", constructionId);
//        if(!Objects.isNull(constructionId)) {
//            Construction construction = generalConstructionService.getConstruction(constructionId);
//            model.addAttribute("construction", construction);
//        }
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);

        return "pages/general/auth/login";
    }
} 