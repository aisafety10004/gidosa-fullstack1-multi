package net.gidosa.full.webadmin.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.services.AuthService;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @GetMapping("/login")
    public String login(
            Model model,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        String test1 = authService.test1();

        model.addAttribute("test1", test1);
        // return PREFIX_THYMELEAF_BASE + "auth/login";
        // return "thymeleaf/auth/login";
        if (principalDetails != null) {
            return "main/main";
        }
        return "auth/login";
    }

    @GetMapping("/login2")
    public String login2(Model model) {
        // String test1 = authService.test1();

        // model.addAttribute("test1", test1);
        // return PREFIX_THYMELEAF_BASE + "auth/login";
        // return "thymeleaf/index2";
        return "index2";
    }

    @GetMapping("/forgot-password")
    public String passwordForgot(Model model) {
        return "auth/forgot-password";
    }
}
