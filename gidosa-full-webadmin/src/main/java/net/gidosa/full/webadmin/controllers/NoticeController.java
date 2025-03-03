package net.gidosa.full.webadmin.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.ConstructionService;
import net.gidosa.full.webadmin.services.NoticeService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;
    private final ConstructionService constructionService;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "noticeDate", direction = Sort.Direction.DESC) Pageable pageable,
                      Model model,
                      @AuthenticationPrincipal UserDetails userDetails) {
        Page<Notice> noticePage;
        
        // 권한에 따라 공지사항 목록 조회
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // 관리자는 모든 공지사항 조회
            noticePage = noticeService.getAllNotices(pageable);
            // 관리자에게는 모든 건설현장 정보 제공 (필터링 목적)
            //model.addAttribute("constructions", constructionService.findAllConstructionsWithManagementMenus());
        } else {
            // 매니저는 자신의 건설현장 공지사항과 전체 공지사항만 조회
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            Construction construction = memberAdmin.getConstruction();
            
            if (construction != null) {
                noticePage = noticeService.getNoticesByConstructionId(construction.getId(), pageable);
                // 매니저에게는 자신의 건설현장 정보만 제공
                //model.addAttribute("construction", construction);
            } else {
                // 건설현장이 없는 매니저는 전체 공지사항만 조회
                noticePage = noticeService.getGlobalNotices(pageable);
            }
        }
        
        model.addAttribute("notices", noticePage);
        return "main/notice/list";
    }

    @GetMapping("/create")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("notice", new Notice());
        
        // 관리자인 경우 건설현장 목록 제공
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            //model.addAttribute("constructions", constructionService.findAllConstructionsWithManagementMenus());
            model.addAttribute("isAdmin", true);
        } else {
            // 매니저인 경우 자신의 건설현장만 제공
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            model.addAttribute("construction", memberAdmin.getConstruction());
            model.addAttribute("isAdmin", false);
        }
        
        return "main/notice/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Notice notice, 
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        @RequestParam(value = "constructionId", required = false) Long constructionId,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            // 매니저인 경우 자신의 건설현장 ID 설정
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction construction = memberAdmin.getConstruction();
                if (construction != null) {
                    constructionId = construction.getId();
                }
            }
            
            noticeService.createNotice(notice, files, constructionId);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공지사항 등록 중 오류가 발생했습니다.");
            log.error("Error creating notice", e);
        }
        return "redirect:/notice";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Notice notice = noticeService.getNoticeById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        
        // 권한 체크: 매니저는 자신의 건설현장 공지사항과 전체 공지사항만 볼 수 있음
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            Construction userConstruction = memberAdmin.getConstruction();
            
            // 공지사항이 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
            if (notice.getConstruction() != null && 
                (userConstruction == null || !notice.getConstruction().getId().equals(userConstruction.getId()))) {
                return "redirect:/notice?error=unauthorized";
            }
        }
        
        model.addAttribute("notice", notice);
        return "main/notice/detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Notice notice = noticeService.getNoticeById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        
        // 권한 체크: 매니저는 자신의 건설현장 공지사항만 수정 가능
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            Construction userConstruction = memberAdmin.getConstruction();
            
            // 공지사항이 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
            if (notice.getConstruction() != null && 
                (userConstruction == null || !notice.getConstruction().getId().equals(userConstruction.getId()))) {
                return "redirect:/notice?error=unauthorized";
            }
        }
        
        model.addAttribute("notice", notice);
        
        // 관리자인 경우 건설현장 목록 제공
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("constructions", constructionService.findAllConstructionsWithManagementMenus());
            model.addAttribute("isAdmin", true);
        } else {
            model.addAttribute("isAdmin", false);
        }
        
        return "main/notice/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, 
                        @ModelAttribute Notice notice,
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        @RequestParam(value = "constructionId", required = false) Long constructionId,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            Notice existingNotice = noticeService.getNoticeById(id)
                    .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
            
            // 권한 체크: 매니저는 자신의 건설현장 공지사항만 수정 가능
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction userConstruction = memberAdmin.getConstruction();
                
                // 공지사항이 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
                if (existingNotice.getConstruction() != null && 
                    (userConstruction == null || !existingNotice.getConstruction().getId().equals(userConstruction.getId()))) {
                    redirectAttributes.addFlashAttribute("error", "해당 공지사항을 수정할 권한이 없습니다.");
                    return "redirect:/notice";
                }
                
                // 매니저는 건설현장을 변경할 수 없음
                notice.setConstruction(existingNotice.getConstruction());
            } else if (constructionId != null) {
                // 관리자는 건설현장 변경 가능
                notice.setConstruction(constructionService.getConstructionWithManagementMenus(constructionId));
            } else {
                // constructionId가 null이면 전체 공지사항으로 설정
                notice.setConstruction(null);
            }
            
            // 파일이 비어있는지 확인하고 필터링
            List<MultipartFile> validFiles = null;
            if (files != null && !files.isEmpty()) {
                validFiles = files.stream()
                                .filter(file -> !file.isEmpty())
                                .toList();
                // 모든 파일이 비어있다면 null로 설정
                if (validFiles.isEmpty()) {
                    validFiles = null;
                }
            }

            noticeService.updateNotice(id, notice, validFiles);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공지사항 수정 중 오류가 발생했습니다.");
            log.error("Error updating notice", e);
        }
        return "redirect:/notice/detail/" + id;
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, 
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            Notice notice = noticeService.getNoticeById(id)
                    .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
            
            // 권한 체크: 매니저는 자신의 건설현장 공지사항만 삭제 가능
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction userConstruction = memberAdmin.getConstruction();
                
                // 공지사항이 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
                if (notice.getConstruction() != null && 
                    (userConstruction == null || !notice.getConstruction().getId().equals(userConstruction.getId()))) {
                    redirectAttributes.addFlashAttribute("error", "해당 공지사항을 삭제할 권한이 없습니다.");
                    return "redirect:/notice";
                }
            }
            
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "공지사항 삭제 중 오류가 발생했습니다.");
            log.error("Error deleting notice", e);
        }
        return "redirect:/notice";
    }
    
    @PostMapping("/attachment/{noticeId}/remove/{attachmentIndex}")
    public String removeAttachment(@PathVariable Long noticeId, 
                                 @PathVariable int attachmentIndex,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            Notice notice = noticeService.getNoticeById(noticeId)
                    .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
            
            // 권한 체크: 매니저는 자신의 건설현장 공지사항의 첨부파일만 삭제 가능
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction userConstruction = memberAdmin.getConstruction();
                
                // 공지사항이 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
                if (notice.getConstruction() != null && 
                    (userConstruction == null || !notice.getConstruction().getId().equals(userConstruction.getId()))) {
                    redirectAttributes.addFlashAttribute("error", "해당 첨부파일을 삭제할 권한이 없습니다.");
                    return "redirect:/notice/detail/" + noticeId;
                }
            }
            
            noticeService.removeAttachment(noticeId, attachmentIndex);
            redirectAttributes.addFlashAttribute("message", "첨부파일이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "첨부파일 삭제 중 오류가 발생했습니다.");
            log.error("Error removing attachment", e);
        }
        return "redirect:/notice/update/" + noticeId;
    }
}
