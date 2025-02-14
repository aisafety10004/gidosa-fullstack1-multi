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
@RequestMapping("/general/document")
public class GeneralDocumentController {
    
    @GetMapping("/terms1")
    public String terms(Model model) {
        model.addAttribute("headerSubInvisible", true);
        return "pages/general/document/terms1";
    }
    
    @GetMapping("/privacy1")
    public String privacy(Model model) {
        model.addAttribute("headerSubInvisible", true);
        return "pages/general/document/privacy1";
    }
    
    @GetMapping("/marketing1")
    public String marketing(Model model) {
        model.addAttribute("headerSubInvisible", true);
        return "pages/general/document/marketing1";
    }
} 