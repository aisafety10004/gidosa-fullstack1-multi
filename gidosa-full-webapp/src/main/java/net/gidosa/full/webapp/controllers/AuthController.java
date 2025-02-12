package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    
    @GetMapping("/login")
    public String loginForm(Model model) {
        return "pages/auth/login";
    }
} 