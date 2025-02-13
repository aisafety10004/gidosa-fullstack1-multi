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