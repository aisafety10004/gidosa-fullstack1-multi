package net.gidosa.full.webapp.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.models.dtos.RequestConstructionDto;
import net.gidosa.full.webapp.services.FileStorageService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import net.gidosa.full.webapp.services.ContactService;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;
    private final FileStorageService fileStorageService;

    @GetMapping("/request-construction")
    public String showContactForm(Model model, HttpSession session) {
//        // 세션에서 메시지 가져오기
//        if (session.getAttribute("flashMessage") != null) {
//            model.addAttribute("message", session.getAttribute("flashMessage"));
//            session.removeAttribute("flashMessage");
//        }
//        if (session.getAttribute("flashError") != null) {
//            model.addAttribute("error", session.getAttribute("flashError"));
//            session.removeAttribute("flashError");
//        }
        return "pages/contact/request-construction";
    }

    @PostMapping("/submit")
    public String submitContactForm(RequestConstructionDto request, 
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            contactService.submitRequest(request);

            String message = "상담이 성공적으로 접수되었습니다.";
            redirectAttributes.addFlashAttribute("message", message);
//            // 세션에도 메시지 저장
//            session.setAttribute("flashMessage", message);
        } catch (Exception e) {
            log.error("Error submitting contact form", e);

            String error = "상담 접수 중 오류가 발생했습니다. 다시 시도해주세요.";
            redirectAttributes.addFlashAttribute("error", error);
//            // 세션에도 에러 메시지 저장
//            session.setAttribute("flashError", error);
        }
        return "redirect:/contact/request-construction";
    }

    @GetMapping("/example")
    public String showContactExample(Model model) {
        // 샘플 건물 데이터 추가
        List<Building> buildings = Arrays.asList(
            new Building("A 건설공사", "/images/building1.jpg"),
            new Building("B 건설공사", "/images/building2.jpg"),
            new Building("C 건설공사", "/images/building3.jpg")
        );
        
        model.addAttribute("buildings", buildings);
        return "pages/contact/example";
    }

    @GetMapping("/search")
    @ResponseBody
    public Map<String, Object> searchConstructions(@RequestParam String keyword) {
        List<Construction> results = contactService.searchByKeyword(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("count", results.size());
        response.put("results", results);
        return response;
    }
    
    /**
     * 파일 다운로드
     * @param fileId 다운로드할 파일 ID
     * @return 파일 다운로드 응답
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // 파일 엔티티 조회
            FileAttachment fileAttachment = fileStorageService.getFile(fileId);
            if (fileAttachment == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 파일 경로 및 Resource 생성
            Path filePath = Paths.get(fileAttachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            // 파일이 존재하고 읽을 수 있는지 확인
            if (!resource.exists() || !resource.isReadable()) {
                log.error("파일을 읽을 수 없습니다: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // 다운로드 헤더 설정
            String contentType = fileAttachment.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }
            
            // 헤더 설정 및 응답 생성
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + fileAttachment.getOriginalFilename() + "\"")
                    .body(resource);
                    
        } catch (IOException ex) {
            log.error("파일 다운로드 중 오류 발생: " + ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * 특정 요청의 특정 유형 파일 다운로드
     * @param requestId 요청 ID
     * @param fileType 파일 유형
     * @return 파일 다운로드 응답
     */
    @GetMapping("/download/request/{requestId}/type/{fileType}")
    public ResponseEntity<Resource> downloadRequestFile(@PathVariable Long requestId, @PathVariable String fileType) {
        try {
            // 파일 엔티티 조회
            FileAttachment fileAttachment = contactService.getRequestFileByType(requestId, fileType);
            if (fileAttachment == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 파일 경로 및 Resource 생성
            Path filePath = Paths.get(fileAttachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());
            
            // 파일이 존재하고 읽을 수 있는지 확인
            if (!resource.exists() || !resource.isReadable()) {
                log.error("파일을 읽을 수 없습니다: {}", filePath);
                return ResponseEntity.notFound().build();
            }
            
            // 다운로드 헤더 설정
            String contentType = fileAttachment.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }
            
            // 헤더 설정 및 응답 생성
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + fileAttachment.getOriginalFilename() + "\"")
                    .body(resource);
                    
        } catch (IOException ex) {
            log.error("파일 다운로드 중 오류 발생: " + ex.getMessage(), ex);
            return ResponseEntity.internalServerError().build();
        }
    }
}

// Building 클래스
@Data
@AllArgsConstructor
class Building {
    private String name;
    private String imageUrl;
} 