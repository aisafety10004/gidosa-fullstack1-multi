package net.gidosa.full.webadmin.controllers.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.FileAttachmentService;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/common/file")
public class FileAttachmentController {
    private final FileAttachmentService fileAttachmentService;

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        try {
            FileAttachment attachment = fileAttachmentService.getFileAttachment(id);
            Resource resource = fileAttachmentService.getFileResource(attachment);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + URLEncoder.encode(attachment.getOriginalFilename(), StandardCharsets.UTF_8) + "\"")
                    .body(resource);
        } catch (Exception e) {
            log.error("파일 다운로드 중 오류 발생", e);
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }
    }
} 