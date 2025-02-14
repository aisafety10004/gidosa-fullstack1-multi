package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/auth")
public class GeneralAuthController {
    
    @GetMapping("/login")
    public String loginForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
        model.addAttribute("headerSubInvisible", true);
        model.addAttribute("constructionId", constructionId);
        model.addAttribute("menuText", "잘한다 건공(A)");

        return "pages/general/auth/login";
    }
} 