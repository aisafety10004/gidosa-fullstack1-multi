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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {
    private final NoticeService noticeService;
    private final ConstructionService constructionService;

    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "noticeDate", direction = Sort.Direction.DESC) Pageable pageable,
                      @RequestParam(required = false) String searchTitle,
                      @RequestParam(required = false) String startDate,
                      @RequestParam(required = false) String endDate,
                      @RequestParam(required = false, defaultValue = "all") String viewMode,
                      @RequestParam(required = false) String sort,
                      @RequestParam(required = false, defaultValue = "asc") String direction,
                      Model model,
                      @AuthenticationPrincipal UserDetails userDetails) {
        Page<Notice> noticePage;
        
        // 정렬 처리
        if (sort != null && !sort.isEmpty()) {
            Sort sortObj = direction.equalsIgnoreCase("desc") ? 
                Sort.by(Sort.Direction.DESC, sort) : 
                Sort.by(Sort.Direction.ASC, sort);
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                sortObj
            );
        }
        
        // 날짜 변환 처리
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        
        if (startDate != null && !startDate.trim().isEmpty()) {
            startDateTime = LocalDate.parse(startDate).atStartOfDay();
        }
        
        if (endDate != null && !endDate.trim().isEmpty()) {
            endDateTime = LocalDate.parse(endDate).atTime(LocalTime.MAX);
        }
        
        // 검색 파라미터를 모델에 추가
        model.addAttribute("searchTitle", searchTitle);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("viewMode", viewMode);
        
        // 정렬 파라미터를 모델에 추가 (필요시 타임리프에서 접근)
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        
        // 권한에 따라 공지사항 목록 조회
//        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
//            // 관리자는 모든 공지사항 검색 가능
//            if (searchTitle != null && !searchTitle.trim().isEmpty()) {
//                if (startDateTime != null || endDateTime != null) {
//                    // 제목과 날짜 범위로 검색
//                    noticePage = noticeService.searchNoticesByTitleAndDateRange(searchTitle, startDateTime, endDateTime, pageable);
//                } else {
//                    // 제목으로만 검색
//                    noticePage = noticeService.searchNoticesByTitle(searchTitle, pageable);
//                }
//            } else if (startDateTime != null || endDateTime != null) {
//                // 날짜 범위로만 검색
//                noticePage = noticeService.searchNoticesByDateRange(startDateTime, endDateTime, pageable);
//            } else {
//                // 검색 조건 없음
//                noticePage = noticeService.getAllNotices(pageable);
//            }
//        } else {
            // 매니저는 자신의 건설현장 공지사항과 전체 공지사항만 조회
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            Construction construction = memberAdmin.getConstruction();

            if (construction != null) {
                Long constructionId = construction.getId();

                if ("unpublishedManager".equals(viewMode)) {
                    // 게시되지 않은 공지사항만 조회 (자신의 건설현장만)
                    if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                        if (startDateTime != null || endDateTime != null) {
                            // 제목과 날짜 범위로 검색 (미게시 공지사항)
                            noticePage = noticeService.searchNoticesByTitleAndDateRangeAndConstructionId(
                                searchTitle, startDateTime, endDateTime, constructionId, pageable);
                        } else {
                            // 제목으로만 검색 (미게시 공지사항)
                            noticePage = noticeService.searchNoticesByTitleAndConstructionId(searchTitle, constructionId, pageable);
                        }
                    } else if (startDateTime != null || endDateTime != null) {
                        // 날짜 범위로만 검색 (미게시 공지사항)
                        noticePage = noticeService.searchNoticesByDateRangeAndConstructionId(startDateTime, endDateTime, constructionId, pageable);
                    } else {
                        // 검색 조건 없음 (미게시 공지사항)
                        noticePage = noticeService.getUnpublishedManagerNoticesByConstructionId(constructionId, pageable);
                    }
                } else if ("publishedAnonymous".equals(viewMode)) {
                    // 익명 사용자에게 게시된 공지사항 조회
                    if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                        if (startDateTime != null || endDateTime != null) {
                            // 제목과 날짜 범위로 검색 (익명 사용자 게시 공지사항)
                            noticePage = noticeService.searchPublishedAnonymousNoticesByTitleAndDateRange(
                                searchTitle, startDateTime, endDateTime, pageable);
                        } else {
                            // 제목으로만 검색 (익명 사용자 게시 공지사항)
                            noticePage = noticeService.searchPublishedAnonymousNoticesByTitle(searchTitle, pageable);
                        }
                    } else if (startDateTime != null || endDateTime != null) {
                        // 날짜 범위로만 검색 (익명 사용자 게시 공지사항)
                        noticePage = noticeService.searchPublishedAnonymousNoticesByDateRange(startDateTime, endDateTime, pageable);
                    } else {
                        // 검색 조건 없음 (익명 사용자 게시 공지사항)
                        noticePage = noticeService.getPublishedAnonymousNotices(pageable);
                    }
                } else if ("publishedLoggedInUser".equals(viewMode)) {
                    // 로그인 사용자에게 게시된 공지사항 조회
                    if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                        if (startDateTime != null || endDateTime != null) {
                            // 제목과 날짜 범위로 검색 (로그인 사용자 게시 공지사항)
                            noticePage = noticeService.searchPublishedLoggedInUserNoticesByTitleAndDateRange(
                                searchTitle, startDateTime, endDateTime, pageable);
                        } else {
                            // 제목으로만 검색 (로그인 사용자 게시 공지사항)
                            noticePage = noticeService.searchPublishedLoggedInUserNoticesByTitle(searchTitle, pageable);
                        }
                    } else if (startDateTime != null || endDateTime != null) {
                        // 날짜 범위로만 검색 (로그인 사용자 게시 공지사항)
                        noticePage = noticeService.searchPublishedLoggedInUserNoticesByDateRange(startDateTime, endDateTime, pageable);
                    } else {
                        // 검색 조건 없음 (로그인 사용자 게시 공지사항)
                        noticePage = noticeService.getPublishedLoggedInUserNotices(pageable);
                    }
                } else {
                    // 게시된 공지사항만 조회 (자신의 건설현장 + 전체 공지사항)
                    if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                        if (startDateTime != null || endDateTime != null) {
                            // 제목과 날짜 범위로 검색 (게시된 공지사항)
                            noticePage = noticeService.searchPublishedManagerNoticesByTitleAndDateRangeAndConstructionId(
                                searchTitle, startDateTime, endDateTime, constructionId, pageable);
                        } else {
                            // 제목으로만 검색 (게시된 공지사항)
                            noticePage = noticeService.searchPublishedManagerNoticesByTitleAndConstructionId(searchTitle, constructionId, pageable);
                        }
                    } else if (startDateTime != null || endDateTime != null) {
                        // 날짜 범위로만 검색 (게시된 공지사항)
                        noticePage = noticeService.searchPublishedManagerNoticesByDateRangeAndConstructionId(startDateTime, endDateTime, constructionId, pageable);
                    } else {
                        // 검색 조건 없음 (게시된 공지사항)
                        noticePage = noticeService.getPublishedManagerNoticesByConstructionId(constructionId, pageable);
                    }
                }
            } else {
                // 건설현장이 없는 매니저는 전체 공지사항만 조회 (construction이 null인 공지사항만)
                if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                    if (startDateTime != null || endDateTime != null) {
                        // 제목과 날짜 범위로 검색 - 전역 공지사항만
                        noticePage = noticeService.searchGlobalNoticesByTitleAndDateRange(
                            searchTitle, startDateTime, endDateTime, pageable);
                    } else {
                        // 제목으로만 검색 - 전역 공지사항만
                        noticePage = noticeService.searchGlobalNoticesByTitle(searchTitle, pageable);
                    }
                } else if (startDateTime != null || endDateTime != null) {
                    // 날짜 범위로만 검색 - 전역 공지사항만
                    noticePage = noticeService.searchGlobalNoticesByDateRange(startDateTime, endDateTime, pageable);
                } else {
                    // 검색 조건 없음 - 전역 공지사항만
                    noticePage = noticeService.getGlobalNotices(pageable);
                }
            }
//        }

        model.addAttribute("notices", noticePage);
        return "main/notice/list";
    }

    @GetMapping("/create")
    public String createForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("notice", new Notice());
        
        MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
        boolean isAdmin = "ROLE_ADMIN".equals(memberAdmin.getRole());

        // 관리자인 경우 건설현장 목록 제공
        if(!isAdmin) {
            model.addAttribute("construction", memberAdmin.getConstruction());
        }
        // if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
        //     //model.addAttribute("constructions", constructionService.findAllConstructionsWithManagementMenus());
        //     model.addAttribute("isAdmin", true);
        // } else {
        //     // 매니저인 경우 자신의 건설현장만 제공
        //     model.addAttribute("construction", memberAdmin.getConstruction());
        //     model.addAttribute("isAdmin", false);
        // }
        model.addAttribute("isAdmin", isAdmin);
        
        return "main/notice/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Notice notice, 
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        @RequestParam(value = "constructionId", required = false) Long constructionId,
                        @RequestParam(value = "mermaidCode", required = false) String mermaidCode,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            // 공지일자가 없으면 현재 시간으로 설정
            if (notice.getNoticeDate() == null) {
                notice.setNoticeDate(LocalDateTime.now());
            }
            
            // 머메이드 코드 설정
            notice.setMermaidCode(mermaidCode);
            
            // 기본적으로 미게시 상태로 설정
            notice.setPublishedManager(false);
            notice.setPublishedAnonymous(false);
            notice.setPublishedLoggedInUser(false);
            
            // 권한에 따라 건설현장 설정
            if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                // 관리자는 모든 건설현장에 대한 공지사항 작성 가능
                if (constructionId != null) {
                    // 특정 건설현장 선택
                    noticeService.createNotice(notice, files, constructionId);
                } else {
                    // 전체 공지사항 (construction_id = null)
                    noticeService.createNotice(notice, files);
                }
            } else {
                // 매니저는 자신의 건설현장에 대한 공지사항만 작성 가능
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction construction = memberAdmin.getConstruction();
                
                if (construction != null) {
                    noticeService.createNotice(notice, files, construction.getId());
                } else {
                    throw new IllegalStateException("건설현장이 없는 매니저는 공지사항을 작성할 수 없습니다.");
                }
            }
            
            redirectAttributes.addFlashAttribute("message", "공지사항이 성공적으로 등록되었습니다.");
            return "redirect:/notice";
        } catch (Exception e) {
            log.error("공지사항 등록 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "공지사항 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/notice/create";
        }
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
                        @RequestParam(value = "mermaidCode", required = false) String mermaidCode,
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

            // Mermaid 코드 설정
            notice.setMermaidCode(mermaidCode);

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
    //@GetMapping("/attachment/{noticeId}/remove/{attachmentIndex}")
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

    @PostMapping("/publish-manager/{id}")
    public String publishManagerNotice(@PathVariable Long id,
                              @RequestParam(defaultValue = "true") boolean publishManager,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        // 권한 확인
        boolean hasPermission = false;
        MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();

        try {
            // 권한 체크: 관리자나 해당 건설현장의 매니저만 게시 상태 변경 가능
            Notice notice = noticeService.getNoticeById(id)
                    .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

            //if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            if (memberAdmin.getRole().equals("ROLE_ADMIN")) {
                hasPermission = true;
            } else {
                Construction userConstruction = memberAdmin.getConstruction();
                
                if (notice.getConstruction() != null && userConstruction != null &&
                    notice.getConstruction().getId().equals(userConstruction.getId())) {
                    hasPermission = true;
                }
            }
            
            if (hasPermission) {
                noticeService.publishManagerNotice(id, publishManager);
                redirectAttributes.addFlashAttribute("message", 
                    publishManager ? "공지사항이 매니저에게 게시되었습니다." : "공지사항이 매니저에게서 게시 취소되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            }
            
            return "redirect:/notice";
        } catch (Exception e) {
            log.error("공지사항 게시 상태 변경 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "공지사항 게시 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/notice";
        }
    }
    
    @PostMapping("/publish-anonymous/{id}")
    public String publishAnonymousNotice(@PathVariable Long id,
                              @RequestParam(defaultValue = "true") boolean publishAnonymous,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        // 권한 확인
        boolean hasPermission = false;
        MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();

        try {
            // 권한 체크: 관리자나 해당 건설현장의 매니저만 게시 상태 변경 가능
            Notice notice = noticeService.getNoticeById(id)
                    .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

            //if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            if (memberAdmin.getRole().equals("ROLE_ADMIN")) {
                hasPermission = true;
            } else {
                Construction userConstruction = memberAdmin.getConstruction();
                
                if (notice.getConstruction() != null && userConstruction != null &&
                    notice.getConstruction().getId().equals(userConstruction.getId())) {
                    hasPermission = true;
                }
            }
            
            if (hasPermission) {
                noticeService.publishAnonymousNotice(id, publishAnonymous);
                redirectAttributes.addFlashAttribute("message", 
                    publishAnonymous ? "공지사항이 익명 사용자에게 게시되었습니다." : "공지사항이 익명 사용자에게서 게시 취소되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            }
            
            return "redirect:/notice";
        } catch (Exception e) {
            log.error("공지사항 익명 사용자 게시 상태 변경 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "공지사항 익명 사용자 게시 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/notice";
        }
    }
    
    @PostMapping("/publish-logged-in/{id}")
    public String publishLoggedInUserNotice(@PathVariable Long id,
                              @RequestParam(defaultValue = "true") boolean publishLoggedInUser,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        // 권한 확인
        boolean hasPermission = false;
        MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();

        try {
            // 권한 체크: 관리자나 해당 건설현장의 매니저만 게시 상태 변경 가능
            Notice notice = noticeService.getNoticeById(id)
                    .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

            //if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            if (memberAdmin.getRole().equals("ROLE_ADMIN")) {
                hasPermission = true;
            } else {
                Construction userConstruction = memberAdmin.getConstruction();
                
                if (notice.getConstruction() != null && userConstruction != null &&
                    notice.getConstruction().getId().equals(userConstruction.getId())) {
                    hasPermission = true;
                }
            }
            
            if (hasPermission) {
                noticeService.publishLoggedInUserNotice(id, publishLoggedInUser);
                redirectAttributes.addFlashAttribute("message", 
                    publishLoggedInUser ? "공지사항이 로그인 사용자에게 게시되었습니다." : "공지사항이 로그인 사용자에게서 게시 취소되었습니다.");
            } else {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            }
            
            return "redirect:/notice";
        } catch (Exception e) {
            log.error("공지사항 로그인 사용자 게시 상태 변경 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "공지사항 로그인 사용자 게시 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/notice";
        }
    }
}
