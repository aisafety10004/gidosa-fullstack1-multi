package net.gidosa.full.webadmin.controllers.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.FileAttachmentService;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/common/file")
public class FileAttachmentController {

    @Value("${file.upload.path}")
//    private String uploadDir;
//    @Value("${org.zerock.upload.path}")
    private String uploadPath;

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

    // 첨부파일 조회 api
    @GetMapping("/view/{filename}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String filename) {
        // 파라미터로 전달된 filename을 가지고 uploadPath폴더 안에 있는 파일들을 찾기
        Resource resource = new FileSystemResource(uploadPath + File.separator + filename);
        String resourceName = resource.getFilename();
        log.info("resourceName: " + resourceName);

        // 응답헤더에 담을 정보를 바꾸기 위해 필요
        HttpHeaders headers = new HttpHeaders();
        try {
            // 응답 헤더로 이미지파일을 표시할 수 있도록 Content-Type값을 변경.
            headers.add("Content-Type", Files.probeContentType(resource.getFile().toPath()));
        } catch(Exception e) {
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok().headers(headers).body(resource);
    }
} 