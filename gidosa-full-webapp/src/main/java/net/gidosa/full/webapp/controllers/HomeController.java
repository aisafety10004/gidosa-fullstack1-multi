package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.common.constants.CommonConsts;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralCustomHtmlService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomHtmlPage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
public class HomeController {
//    @Value("${app.version}")
//    private String appVersion;
    @Value("${file.upload.path}")
    private String uploadDir;

    private final GeneralCustomHtmlService generalCustomHtmlService;
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;

    @GetMapping({"/", "/home"})
    public String index(Model model) throws IOException {
        // construnctionId가 비어 있으면서 MainPage인 custom_html페이지 조회
        List<CustomHtmlPage> customHtmlPageList
                = generalCustomHtmlService.getCustomHtmlMainPagesByConstructionId(true, null);

        if (customHtmlPageList != null && customHtmlPageList.size() > 0) {
            Path filePath = Paths.get(uploadDir)
                    .resolve(CommonConsts.CUSTOM_HTML_FOLOER + File.separator + customHtmlPageList.get(0).getHtmlFile().getStoredFilename());
            if (!Files.exists(filePath)) {
                return "error/404";
            }
            String htmlContent = Files.readString(filePath); // Java 11 이상
            model.addAttribute("htmlContent", htmlContent);
        }

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
