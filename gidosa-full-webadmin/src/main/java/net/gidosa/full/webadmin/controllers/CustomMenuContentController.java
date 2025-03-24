package net.gidosa.full.webadmin.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType1;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType2;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType3;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType4;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType5;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.full.webadmin.services.CustomMenuContentService;
import net.gidosa.full.webadmin.services.CustomMenuService;
import net.gidosa.full.webadmin.services.FileAttachmentService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/custom")
public class CustomMenuContentController {
    
    private final CustomMenuService customMenuService;
    private final CustomMenuContentService customMenuContentService;
    private final FileAttachmentService fileAttachmentService;
    
    /**
     * 메뉴에 대한 접근 권한을 확인합니다.
     */
    private boolean hasAccess(Long menuId) {
        try {
            // 현재 로그인한 사용자 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
            Construction construction = memberAdmin.getConstruction();
            boolean isAdmin = "ROLE_ADMIN".equals(memberAdmin.getRole());
            Long constructionId = Objects.isNull(construction) ? null : construction.getId();
            
            // 메뉴 정보 조회
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 관리자이거나 건설현장이 일치하는 경우에만 접근 허용
            if (isAdmin) {
                return true;
            } else if (!Objects.isNull(constructionId) && menu.getConstruction() != null && 
                        menu.getConstruction().getId().equals(constructionId)) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            log.error("접근 권한 확인 중 오류 발생", e);
            return false;
        }
    }
    
    /**
     * 커스텀 메뉴 컨텐츠 페이지를 표시합니다.
     */
    @GetMapping("/**")
    public String handleCustomMenuRequest(HttpServletRequest request, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        // 현재 요청 URL 가져오기 - HttpServletRequest를 사용하여 직접 경로 추출
        String requestURI = request.getRequestURI();
        log.info("Requested URI: {}", requestURI);

        // 메뉴 정보 조회 - 전체 경로로 조회
        CustomMenu menu;
        if(Objects.isNull(constructionId)) {
            menu = customMenuService.getMenuByConstructionIsNullAndUrl(requestURI);
        } else {
            menu = customMenuService.getMenuByUrl(constructionId, requestURI);
        }

        if (menu == null) {
            log.error("Menu not found for URL: {}", requestURI);
            return "redirect:/main?error=menu-not-found";
        }
        
        model.addAttribute("menu", menu);
        
        // 메뉴 타입에 따라 다른 처리
        if (menu.getMenuType() == 1) { // 단건 내용 저장/보기
            CustomMenuContentType1 content = customMenuContentService.getType1ContentByMenuId(menu.getId());
            model.addAttribute("content", content != null ? content : new CustomMenuContentType1());
            return "main/custom/type1";
        } else if (menu.getMenuType() == 2) { // 날짜 저장/보기
            LocalDate selectedDate = LocalDate.now();
            CustomMenuContentType2 content = customMenuContentService.getType2ContentByMenuIdAndDate(menu.getId(), selectedDate);
            model.addAttribute("content", content != null ? content : new CustomMenuContentType2());
            model.addAttribute("selectedDate", selectedDate);
            return "main/custom/type2";
        } else if (menu.getMenuType() == 3) { // Mermaid 저장/보기
            CustomMenuContentType3 content = customMenuContentService.getType3ContentByMenuId(menu.getId());
            model.addAttribute("content", content != null ? content : new CustomMenuContentType3());
            return "main/custom/type3";
        } else if (menu.getMenuType() == 4) { // 단건 내용(With 첨부파일) 저장/보기
            CustomMenuContentType4 content = customMenuContentService.getType4ContentByMenuId(menu.getId());
            model.addAttribute("content", content != null ? content : new CustomMenuContentType4());
            return "main/custom/type4";
        } else if (menu.getMenuType() == 5) { // Mermaid(With 첨부파일) 저장/보기
            CustomMenuContentType5 content = customMenuContentService.getType5ContentByMenuId(menu.getId());
            model.addAttribute("content", content != null ? content : new CustomMenuContentType5());
            return "main/custom/type5";
        } else {
            return "redirect:/main?error=invalid-menu-type";
        }
    }
    
    /**
     * 타입1 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type1/{menuId}")
    public String saveType1Content(@PathVariable Long menuId,
                                  @RequestParam String content,
                                  RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));

            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }

            customMenuContentService.saveType1Content(menuId, content);
            redirectAttributes.addFlashAttribute("message", "내용이 저장되었습니다.");
            
            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입1 컨텐츠 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }
    
    /**
     * 타입2 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type2/{menuId}")
    public String saveType2Content(@PathVariable Long menuId,
                                  @RequestParam String content,
                                  @RequestParam String selectedDate,
                                  RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            LocalDate date = LocalDate.parse(selectedDate);
            customMenuContentService.saveType2Content(menuId, content, date);
            redirectAttributes.addFlashAttribute("message", "내용이 저장되었습니다.");
            
            return "redirect:" + menu.getUrl() + "?date=" + selectedDate;
        } catch (Exception e) {
            log.error("타입2 컨텐츠 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }
    
    /**
     * 타입2 메뉴에서 날짜를 선택했을 때 해당 날짜의 컨텐츠를 조회합니다.
     */
    @GetMapping("/type2/{menuId}")
    public String getType2ContentByDate(@PathVariable Long menuId,
                                       @RequestParam(required = false) String date,
                                       Model model,
                                       RedirectAttributes redirectAttributes,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            CustomMenuContentType2 content = customMenuContentService.getType2ContentByMenuIdAndDate(menuId, selectedDate);
            
            model.addAttribute("menu", menu);
            model.addAttribute("content", content != null ? content : new CustomMenuContentType2());
            model.addAttribute("selectedDate", selectedDate);
            
            return "main/custom/type2";
        } catch (Exception e) {
            log.error("타입2 컨텐츠 조회 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 조회 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입1 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type1/{menuId}")
    public String deleteType1Content(@PathVariable Long menuId,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            customMenuContentService.deleteType1Content(menuId);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");
            
            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입1 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입2 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type2/{menuId}")
    public String deleteType2Content(@PathVariable Long menuId,
                                   @RequestParam String date,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            LocalDate selectedDate = LocalDate.parse(date);
            customMenuContentService.deleteType2Content(menuId, selectedDate);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");
            
            return "redirect:" + menu.getUrl() + "?date=" + date;
        } catch (Exception e) {
            log.error("타입2 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }
    
    /**
     * 타입3 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type3/{menuId}")
    public String saveType3Content(@PathVariable Long menuId,
//                                 @RequestParam String content,
                                 @RequestParam(value = "mermaidCode", required = false) String mermaidCode,
                                 RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();

        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));

            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }

            customMenuContentService.saveType3Content(menuId, mermaidCode);
            redirectAttributes.addFlashAttribute("message", "내용이 저장되었습니다.");

            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입3 컨텐츠 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입3 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type3/{menuId}")
    public String deleteType3Content(@PathVariable Long menuId,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();

        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));

            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }

            customMenuContentService.deleteType3Content(menuId);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");

            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입3 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입4 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type4/{menuId}")
    public String saveType4Content(@PathVariable Long menuId,
                                 @RequestParam("content") String content,
                                 @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                 RedirectAttributes redirectAttributes) {
        if (!hasAccess(menuId)) {
            return "redirect:/error/unauthorized";
        }

        CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));

        try {
            // 컨텐츠 저장 (첨부파일 함께 처리)
            customMenuContentService.saveType4Content(menuId, content, files);
            redirectAttributes.addFlashAttribute("success", "컨텐츠가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            log.error("Error saving Type4 content", e);
            redirectAttributes.addFlashAttribute("error", "컨텐츠 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:" + menu.getUrl();
    }

    /**
     * 타입5 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type5/{menuId}")
    public String saveType5Content(@PathVariable Long menuId,
                                 @RequestParam("mermaidCode") String mermaidCode,
                                 @RequestParam(value = "files", required = false) List<MultipartFile> files,
                                 RedirectAttributes redirectAttributes) {
        if (!hasAccess(menuId)) {
            return "redirect:/error/unauthorized";
        }
        
        CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
                    
        try {
            // 컨텐츠 저장 (첨부파일 함께 처리)
            customMenuContentService.saveType5Content(menuId, mermaidCode, files);
            redirectAttributes.addFlashAttribute("success", "머메이드 다이어그램이 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            log.error("Error saving Type5 content", e);
            redirectAttributes.addFlashAttribute("error", "머메이드 다이어그램 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:" + menu.getUrl();
    }

    /**
     * 타입4 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type4/{menuId}")
    public String deleteType4Content(@PathVariable Long menuId,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            customMenuContentService.deleteType4Content(menuId);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");
            
            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입4 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입5 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type5/{menuId}")
    public String deleteType5Content(@PathVariable Long menuId,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
//        if (construction == null) {
//            return "redirect:/main?error=no-construction";
//        }
        Long constructionId = Objects.isNull(construction) ? null : construction.getId();
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!Objects.isNull(constructionId) && !menu.getConstruction().getId().equals(constructionId)) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            customMenuContentService.deleteType5Content(menuId);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");
            
            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입5 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 첨부파일 다운로드
     */
    @GetMapping("/download/file/{contentId}/{fileNum}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long contentId,
                                              @PathVariable Integer fileNum,
                                              HttpServletResponse response) {
        try {
            // fileNum에 따라 파일 선택 (1, 2, 3 중 하나)
            FileAttachment attachment = null;
            
            // 서비스 메서드를 통해 첨부파일이 포함된 콘텐츠 조회
            CustomMenuContentType4 type4Content = customMenuContentService.getType4ContentWithAttachmentsById(contentId);

            if (type4Content != null) {
                if (fileNum == 1 && type4Content.getFileAttachment1() != null) {
                    attachment = type4Content.getFileAttachment1();
                } else if (fileNum == 2 && type4Content.getFileAttachment2() != null) {
                    attachment = type4Content.getFileAttachment2();
                } else if (fileNum == 3 && type4Content.getFileAttachment3() != null) {
                    attachment = type4Content.getFileAttachment3();
                }
            } else {
                // Type5도 확인
                CustomMenuContentType5 type5Content = customMenuContentService.getType5ContentWithAttachmentsById(contentId);
                if (type5Content != null) {
                    if (fileNum == 1 && type5Content.getFileAttachment1() != null) {
                        attachment = type5Content.getFileAttachment1();
                    } else if (fileNum == 2 && type5Content.getFileAttachment2() != null) {
                        attachment = type5Content.getFileAttachment2();
                    } else if (fileNum == 3 && type5Content.getFileAttachment3() != null) {
                        attachment = type5Content.getFileAttachment3();
                    }
                }
            }
            
            if (attachment == null) {
                return ResponseEntity.notFound().build();
            }
            
            // 파일 다운로드 경로 설정
            String filePath = attachment.getFilePath();
            File file = new File(filePath);
            
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // 한글 파일명을 위한 인코딩 처리
            String encodedFileName = URLEncoder.encode(attachment.getOriginalFilename(), StandardCharsets.UTF_8.toString())
                                            .replaceAll("\\+", "%20");
            
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encodedFileName)
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .contentLength(file.length())
                    .body(resource);
            
        } catch (Exception e) {
            log.error("첨부파일 다운로드 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 