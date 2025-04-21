package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralNoticeService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/notice")
public class GeneralNoticeController {
    private final GeneralNoticeService noticeService;
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;
    /**
     * 공지사항 목록 페이지
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "searchType", required = false, defaultValue = "title") String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "constructionId", required = false) Long constructionId,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && 
                             !authentication.getName().equals("anonymousUser");

        // 로그인 상태에 따라 적절한 공지사항 목록 조회
        Page<Notice> noticePage;
        
        if (constructionId != null) {
            if (searchKeyword != null && !searchKeyword.isEmpty()) {
                if ("title".equals(searchType)) {
                    if (isLoggedIn) {
                        noticePage = noticeService.searchLoggedInUserNoticesByTitleAndConstructionId(searchKeyword, constructionId, pageable);
                    } else {
                        noticePage = noticeService.searchAnonymousNoticesByTitleAndConstructionId(searchKeyword, constructionId, pageable);
                    }
                } else { // "content"
                    if (isLoggedIn) {
                        noticePage = noticeService.searchLoggedInUserNoticesByContentAndConstructionId(searchKeyword, constructionId, pageable);
                    } else {
                        noticePage = noticeService.searchAnonymousNoticesByContentAndConstructionId(searchKeyword, constructionId, pageable);
                    }
                }
            } else {
                if (isLoggedIn) {
                    noticePage = noticeService.getPublishedLoggedInUserNoticesByConstructionId(constructionId, pageable);
                } else {
                    noticePage = noticeService.getPublishedAnonymousNoticesByConstructionId(constructionId, pageable);
                }
            }
        } else {
            if (searchKeyword != null && !searchKeyword.isEmpty()) {
                if ("title".equals(searchType)) {
                    if (isLoggedIn) {
                        noticePage = noticeService.searchPublishedLoggedInUserNoticesByTitle(searchKeyword, pageable);
                    } else {
                        noticePage = noticeService.searchPublishedAnonymousNoticesByTitle(searchKeyword, pageable);
                    }
                } else { // "content"
                    if (isLoggedIn) {
                        noticePage = noticeService.searchPublishedLoggedInUserNoticesByContent(searchKeyword, pageable);
                    } else {
                        noticePage = noticeService.searchPublishedAnonymousNoticesByContent(searchKeyword, pageable);
                    }
                }
            } else {
                if (isLoggedIn) {
                    noticePage = noticeService.getPublishedLoggedInUserNotices(pageable);
                } else {
                    noticePage = noticeService.getPublishedAnonymousNotices(pageable);
                }
            }
        }

        model.addAttribute("noticePage", noticePage);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("constructionId", constructionId);

        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);

        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/notice/list";
    }

    /**
     * 공지사항 상세 페이지
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id,
                         @RequestParam(value = "constructionId", required = false) Long constructionId,
                         Model model) {
        Notice notice = noticeService.getNoticeById(id);

        // 접근 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && 
                            !authentication.getName().equals("anonymousUser");

        if ((isLoggedIn && !notice.getPublishedLoggedInUser()) || 
            (!isLoggedIn && !notice.getPublishedAnonymous())) {
            return "redirect:/general/notice/list";
        }

        model.addAttribute("notice", notice);

//        Construction construction = generalConstructionService.getConstruction(constructionId);
        Construction construction = notice.getConstruction();
        if(Objects.isNull(construction))
            construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);

        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/notice/detail";
    }
    
    /**
     * 공지사항 상세 정보를 JSON으로 반환하는 API
     */
    @GetMapping("/{id}/details")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNoticeDetails(
            @PathVariable("id") Long id,
            @RequestParam(value = "constructionId", required = false) Long constructionId
    ) {
        Notice notice = noticeService.getNoticeById(id);
        
        // 접근 권한 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isLoggedIn = authentication != null && authentication.isAuthenticated() && 
                            !authentication.getName().equals("anonymousUser");

        if ((isLoggedIn && !notice.getPublishedLoggedInUser()) || 
            (!isLoggedIn && !notice.getPublishedAnonymous())) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("id", notice.getId());
        response.put("title", notice.getTitle());
        response.put("content", notice.getContent());
        response.put("createdAt", notice.getCreatedAt());
        response.put("updatedAt", notice.getUpdatedAt());
        
        // 첨부파일 정보
        List<Map<String, Object>> attachments = notice.getAttachments().stream()
                .map(attachment -> {
                    Map<String, Object> attachmentMap = new HashMap<>();
                    attachmentMap.put("id", attachment.getId());
                    attachmentMap.put("originalName", attachment.getOriginalFilename());
                    attachmentMap.put("fileSize", attachment.getFileSize());
                    return attachmentMap;
                })
                .collect(Collectors.toList());
        
        response.put("attachments", attachments);

        Construction noticeConstruction = notice.getConstruction();
        // 공사현장 정보
        if (!Objects.isNull(noticeConstruction)) {
            Map<String, Object> construction = new HashMap<>();
            construction.put("id", notice.getConstruction().getId());
            construction.put("name", notice.getConstruction().getName());
            response.put("construction", construction);
        } else {
            response.put("construction", generalConstructionService.getConstruction(constructionId));
        }
        
        // 머메이드 코드 정보
        if (notice.getMermaidCode() != null && !notice.getMermaidCode().isEmpty()) {
            response.put("mermaidCode", notice.getMermaidCode());
        }
        
        return ResponseEntity.ok(response);
    }
} 