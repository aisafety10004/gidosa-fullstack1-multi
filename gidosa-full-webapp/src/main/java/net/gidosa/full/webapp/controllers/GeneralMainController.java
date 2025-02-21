package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import net.gidosa.full.webapp.services.GeneralMainService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/main")
public class GeneralMainController {
    private final GeneralMainService generalMainService;

    @GetMapping()
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return "general/main/main";
    }



}
