package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import net.gidosa.common.constants.CommonConsts;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralCustomHtmlService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@RequiredArgsConstructor
@RequestMapping("/custom-viewer/htmls")
public class HtmlViewerController {
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;

    @Value("${file.upload.path}")
    private String uploadDir;

    @GetMapping("/{fileId}")
    public String viewUserHtml(@PathVariable String fileId, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser");

        Long constructionId = null;
        if (isLoggedIn) {
            constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
            Construction construction = generalConstructionService.getConstruction(constructionId);
            model.addAttribute("construction", construction);
        } else {
            return "error/403";
        }

        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        //final Path uploadPath = Paths.get("uploaded-html");
//        final Path uploadPath = Paths.get(uploadDir);
//        Path filePath = uploadPath.resolve(fileId + ".html");
        Path filePath = Paths.get(uploadDir).resolve(CommonConsts.CUSTOM_HTML_FOLOER + File.separator + fileId);

        if (!Files.exists(filePath)) {
            return "error/404";
        }

        String htmlContent = Files.readString(filePath); // Java 11 이상
        
        // HTML에서 제목 추출 (정규식 사용)
        String title = extractTitleFromHtml(htmlContent);
        
        // title이 존재하면 모델에 추가
        if (title != null && !title.isEmpty()) {
            model.addAttribute("title", title);
        }
        
//       htmlContent = Jsoup.clean(htmlContent, Safelist.relaxed()); // script 및 css 제외
        model.addAttribute("htmlContent", htmlContent);

        return "custom-viewer/html-view";
    }
    
    /**
     * HTML 문자열에서 title 태그 내용을 추출합니다.
     * 
     * @param html HTML 문자열
     * @return 추출된 title 또는 빈 문자열
     */
    private String extractTitleFromHtml(String html) {
        // title 태그를 추출하는 정규식 패턴
        Pattern pattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "";
    }
}
