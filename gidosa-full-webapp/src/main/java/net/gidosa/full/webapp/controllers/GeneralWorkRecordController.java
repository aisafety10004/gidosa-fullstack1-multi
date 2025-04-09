package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/work-record")
public class GeneralWorkRecordController {
    @GetMapping("/start")
    public String start(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return "pages/general/work-record/start";
    }

    @GetMapping("/leave")
    public String leave(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return "pages/general/work-record/leave";
    }
}
