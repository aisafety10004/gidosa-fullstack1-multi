package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
public class HomeController {
    @GetMapping({"/", "/home"})
    public String index(Model model) {
        return "pages/home";
    }
}
