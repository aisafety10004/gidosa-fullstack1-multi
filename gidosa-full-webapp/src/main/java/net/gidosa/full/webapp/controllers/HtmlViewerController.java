package net.gidosa.full.webapp.controllers;

import net.gidosa.common.constants.CommonConsts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/custom-viewer/htmls")
public class HtmlViewerController {

    @Value("${file.upload.path}")
    private String uploadDir;

    @GetMapping("/{fileId}")
    public String viewUserHtml(@PathVariable String fileId, Model model) throws IOException {
        //final Path uploadPath = Paths.get("uploaded-html");
//        final Path uploadPath = Paths.get(uploadDir);
//        Path filePath = uploadPath.resolve(fileId + ".html");
        Path filePath = Paths.get(uploadDir).resolve(CommonConsts.CUSTOM_HTML_FOLOER + File.separator + fileId);

        if (!Files.exists(filePath)) {
            return "error/404";
        }

        String htmlContent = Files.readString(filePath); // Java 11 이상
//       htmlContent = Jsoup.clean(htmlContent, Safelist.relaxed()); // script 및 css 제외
        model.addAttribute("htmlContent", htmlContent);

        return "custom-viewer/html-view";
    }
}
