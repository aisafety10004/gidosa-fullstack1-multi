package net.gidosa.full.webadmin.controllers.common;

import net.gidosa.common.constants.CommonConsts;
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

@Controller
@RequestMapping("/file-viewer")
public class FileViewerController {
    @Value("${file.upload.path}")
    private String uploadPath;

    @GetMapping("/html-view/{fileId}")
    public String viewUserHtml(@PathVariable String fileId, Model model) throws IOException {
//        Path filePath = Paths.get(uploadPath).resolve(fileId + ".html");

        //final Path uploadPath = Paths.get("uploaded-html");
//        final Path uploadPath = Paths.get(uploadDir);
//        Path filePath = uploadPath.resolve(fileId + ".html");
        Path filePath = Paths.get(uploadPath).resolve(CommonConsts.CUSTOM_HTML_FOLOER + File.separator + fileId);

        if (!Files.exists(filePath)) {
            return "error/404";
        }

        String htmlContent = Files.readString(filePath); // Java 11 이상
//       htmlContent = Jsoup.clean(htmlContent, Safelist.relaxed()); // script 및 css 제외
        model.addAttribute("htmlContent", htmlContent);

        return "custom-viewer/html-view";
    }
}
