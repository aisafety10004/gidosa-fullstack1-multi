package net.gidosa.full.webapp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/document")
public class DocumentController {
    
    @GetMapping("/terms1")
    public String terms(Model model) {
        return "pages/document/terms1";
    }
    
    @GetMapping("/privacy1")
    public String privacy(Model model) {
        return "pages/document/privacy1";
    }
    
    @GetMapping("/marketing1")
    public String marketing(Model model) {
        return "pages/document/marketing1";
    }
} 