package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import net.gidosa.full.webapp.services.GeneralMainService;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/main")
public class GeneralMainController {
    private final GeneralMainService generalMainService;
    private final GeneralConstructionService generalConstructionService;

    @GetMapping("/main")
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();

        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);

        return "pages/general/main/main";
    }

    @GetMapping("/main2")
    public String index2(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();

        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);

        return "pages/general/main/main2";
    }
}
