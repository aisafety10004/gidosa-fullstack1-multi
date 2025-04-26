package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.common.constants.CommonConsts;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralCustomHtmlService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomHtmlPage;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general")
public class GeneralConstructionController {
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;
    private final GeneralCustomHtmlService generalCustomHtmlService;

    @Value("${file.upload.path}")
    private String uploadDir;

    @GetMapping("/construction/{constructionId}")
    public String index(@PathVariable Long constructionId, Model model) throws IOException {
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        // MainPage인 custom_html페이지 조회
        List<CustomHtmlPage> customHtmlPageList
                = generalCustomHtmlService.getCustomHtmlMainPagesByConstructionId(true, constructionId);

        if (customHtmlPageList != null && customHtmlPageList.size() > 0) {
            Path filePath = Paths.get(uploadDir)
                    .resolve(CommonConsts.CUSTOM_HTML_FOLOER + File.separator + customHtmlPageList.get(0).getHtmlFile().getStoredFilename());
            if (!Files.exists(filePath)) {
                return "error/404";
            }
            String htmlContent = Files.readString(filePath); // Java 11 이상
            model.addAttribute("htmlContent", htmlContent);
        }

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
