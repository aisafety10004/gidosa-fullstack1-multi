package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general")
public class GeneralConstructionController {
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;

    @GetMapping("/construction/{constructionId}")
    public String index(@PathVariable Long constructionId, Model model) {
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/construction";
    }

    @GetMapping("/construction2/{constructionId}")
    public String index2(@PathVariable Long constructionId, Model model) {
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/construction2";
    }
}
