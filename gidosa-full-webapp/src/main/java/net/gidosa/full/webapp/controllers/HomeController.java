package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
public class HomeController {
//    @Value("${app.version}")
//    private String appVersion;

    @GetMapping({"/", "/home"})
    public String index(Model model) {
        return "pages/home";
    }

//    @GetMapping(value = "/manifest.json", produces = MediaType.APPLICATION_JSON_VALUE)
//    public String manifest(
////            @RequestParam(required = false, defaultValue = "guest") String userId,
//            Model model
//    ) {
//        String version = "1.0.0"; // 또는 @Value로 주입받을 수도 있음
//        model.addAttribute("version", version);
////        model.addAttribute("userId", userId);
//        model.addAttribute("constructionId", "1");
//        return "manifest";
//    }
}
