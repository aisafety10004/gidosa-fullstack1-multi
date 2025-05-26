package net.gidosa.full.webadmin.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.EducationTemplateService;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/education/template")
public class EducationTemplateController {

    private final EducationTemplateService educationTemplateService;

    @GetMapping("/")
    public String list(Model model) {
        return "main/exam/education/template";
    }

}
