package net.gidosa.full.webadmin.controllers.managers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.ConstructionService;
import net.gidosa.full.webadmin.services.WorkDiscussionService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.models.entities.dbs.mysql.WorkDiscussion;
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
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/process/work-discussion")
public class WorkDiscussionController {
    private final WorkDiscussionService workDiscussionService;
    private final ConstructionService constructionService;
    
    // 기본 그룹 목록 정의
    //private final List<String> DEFAULT_GROUPS = Arrays.asList("일반", "안전", "품질", "공정", "자재", "인력");
    private final List<String> DEFAULT_GROUPS = Arrays.asList("발주자", "시공사", "관리자", "경영자");
    private final String ALL_GROUP = "All";

    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/process/work-discussion/list";
    }

    @GetMapping("/list")
    public String list(@PageableDefault(size = 10, sort = "discussionDate", direction = Sort.Direction.DESC) Pageable pageable,
                      @RequestParam(required = false) String searchTitle,
                      @RequestParam(required = false) String startDate,
                      @RequestParam(required = false) String endDate,
                      @RequestParam(required = false, defaultValue = "발주자") String groupName,
                      @RequestParam(required = false) String sort,
                      @RequestParam(required = false) String direction,
                      Model model,
                      @AuthenticationPrincipal UserDetails userDetails) {
        Page<WorkDiscussion> workDiscussionPage;
        
        // 정렬 파라미터 처리
        if (sort != null && !sort.isEmpty() && direction != null && !direction.isEmpty()) {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            pageable = org.springframework.data.domain.PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                Sort.by(sortDirection, sort)
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
        model.addAttribute("groupName", groupName);
        model.addAttribute("groups", DEFAULT_GROUPS);
        model.addAttribute("allGroup", ALL_GROUP);
        model.addAttribute("showCreateButton", !ALL_GROUP.equals(groupName));
        
        // 매니저는 자신의 건설현장 업무협의와 전체 업무협의만 조회
        MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();

        if (construction != null) {
            Long constructionId = construction.getId();

            // All 그룹인 경우 모든 그룹의 업무협의 조회
            if (ALL_GROUP.equals(groupName)) {
                if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                    if (startDateTime != null || endDateTime != null) {
                        // 제목과 날짜 범위로 검색 (그룹 무관)
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitleAndDateRangeAndConstructionId(
                            searchTitle, startDateTime, endDateTime, constructionId, pageable);
                    } else {
                        // 제목으로 검색 (그룹 무관)
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitleAndConstructionId(
                            searchTitle, constructionId, pageable);
                    }
                } else if (startDateTime != null || endDateTime != null) {
                    // 날짜 범위로 검색 (그룹 무관)
                    workDiscussionPage = workDiscussionService.searchWorkDiscussionsByDateRangeAndConstructionId(
                        startDateTime, endDateTime, constructionId, pageable);
                } else {
                    // 모든 그룹의 업무협의 조회
                    workDiscussionPage = workDiscussionService.getWorkDiscussionsByConstructionId(constructionId, pageable);
                }
            } else {
                // 특정 그룹의 업무협의 조회
                if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                    if (startDateTime != null || endDateTime != null) {
                        // 제목과 날짜 범위, 그룹으로 검색
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitleAndDateRangeAndConstructionIdAndGroup(
                            searchTitle, startDateTime, endDateTime, constructionId, groupName, pageable);
                    } else {
                        // 제목과 그룹으로 검색
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitleAndConstructionIdAndGroup(
                            searchTitle, constructionId, groupName, pageable);
                    }
                } else if (startDateTime != null || endDateTime != null) {
                    // 날짜 범위와 그룹으로 검색
                    workDiscussionPage = workDiscussionService.searchWorkDiscussionsByDateRangeAndConstructionIdAndGroup(
                        startDateTime, endDateTime, constructionId, groupName, pageable);
                } else {
                    // 그룹으로만 검색
                    workDiscussionPage = workDiscussionService.getWorkDiscussionsByConstructionIdAndGroup(constructionId, groupName, pageable);
                }
            }
        } else {
            // 건설현장이 없는 매니저는 전체 업무협의만 조회 (construction이 null인 업무협의만)
            if (ALL_GROUP.equals(groupName)) {
                if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                    if (startDateTime != null || endDateTime != null) {
                        // 제목과 날짜 범위로 검색 (그룹 무관)
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitleAndDateRange(
                            searchTitle, startDateTime, endDateTime, pageable);
                    } else {
                        // 제목으로 검색 (그룹 무관)
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitle(searchTitle, pageable);
                    }
                } else if (startDateTime != null || endDateTime != null) {
                    // 날짜 범위로 검색 (그룹 무관)
                    workDiscussionPage = workDiscussionService.searchWorkDiscussionsByDateRange(
                        startDateTime, endDateTime, pageable);
                } else {
                    // 모든 그룹의 업무협의 조회
                    workDiscussionPage = workDiscussionService.getGlobalWorkDiscussions(pageable);
                }
            } else {
                if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                    if (startDateTime != null || endDateTime != null) {
                        // 제목과 날짜 범위, 그룹으로 검색
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitleAndDateRangeAndGroup(
                            searchTitle, startDateTime, endDateTime, groupName, pageable);
                    } else {
                        // 제목과 그룹으로 검색
                        workDiscussionPage = workDiscussionService.searchWorkDiscussionsByTitleAndGroup(searchTitle, groupName, pageable);
                    }
                } else if (startDateTime != null || endDateTime != null) {
                    // 날짜 범위와 그룹으로 검색
                    workDiscussionPage = workDiscussionService.searchWorkDiscussionsByDateRangeAndGroup(
                        startDateTime, endDateTime, groupName, pageable);
                } else {
                    // 그룹으로만 검색
                    workDiscussionPage = workDiscussionService.getWorkDiscussionsByGroup(groupName, pageable);
                }
            }
        }

        model.addAttribute("workDiscussions", workDiscussionPage);
        return "main/process/work-discussion/list";
    }

    @GetMapping("/create")
    public String createForm(Model model, 
                           @RequestParam(required = false) String groupName,
                           @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("workDiscussion", new WorkDiscussion());
        model.addAttribute("groups", DEFAULT_GROUPS);
        model.addAttribute("selectedGroup", groupName);
        
        // 관리자인 경우 건설현장 목록 제공
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            model.addAttribute("constructions", constructionService.findAllConstructionsWithManagementMenus());
            model.addAttribute("isAdmin", true);
        } else {
            // 매니저인 경우 자신의 건설현장만 제공
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            model.addAttribute("construction", memberAdmin.getConstruction());
            model.addAttribute("isAdmin", false);
        }
        
        return "main/process/work-discussion/create";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute WorkDiscussion workDiscussion, 
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        @RequestParam(value = "constructionId", required = false) Long constructionId,
                        @RequestParam(value = "mermaidCode", required = false) String mermaidCode,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            // 협의일자가 없으면 현재 시간으로 설정
            if (workDiscussion.getDiscussionDate() == null) {
                workDiscussion.setDiscussionDate(LocalDateTime.now());
            }
            
            // 그룹명이 없으면 기본값으로 설정
            if (workDiscussion.getGroupName() == null || workDiscussion.getGroupName().isEmpty()) {
                workDiscussion.setGroupName(DEFAULT_GROUPS.get(0)); // 첫 번째 그룹을 기본값으로 사용
            }
            
            // 머메이드 코드 설정
            workDiscussion.setMermaidCode(mermaidCode);
            
            // 권한에 따라 건설현장 설정
            if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                // 관리자는 모든 건설현장에 대한 업무협의 작성 가능
                if (constructionId != null) {
                    // 특정 건설현장 선택
                    workDiscussionService.createWorkDiscussion(workDiscussion, files, constructionId);
                } else {
                    // 전체 업무협의 (construction_id = null)
                    workDiscussionService.createWorkDiscussion(workDiscussion, files);
                }
            } else {
                // 매니저는 자신의 건설현장에 대한 업무협의만 작성 가능
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction construction = memberAdmin.getConstruction();
                
                if (construction != null) {
                    workDiscussionService.createWorkDiscussion(workDiscussion, files, construction.getId());
                } else {
                    throw new IllegalStateException("건설현장이 없는 매니저는 업무협의를 작성할 수 없습니다.");
                }
            }
            
            redirectAttributes.addFlashAttribute("message", "업무협의가 성공적으로 등록되었습니다.");
            // URL 인코딩 처리
            String encodedGroupName = UriUtils.encode(workDiscussion.getGroupName(), StandardCharsets.UTF_8);
            return "redirect:/process/work-discussion/list?groupName=" + encodedGroupName;
        } catch (Exception e) {
            log.error("업무협의 등록 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "업무협의 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/process/work-discussion/create";
        }
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        WorkDiscussion workDiscussion = workDiscussionService.getWorkDiscussionById(id)
                .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));
        
        // 권한 체크: 매니저는 자신의 건설현장 업무협의와 전체 업무협의만 볼 수 있음
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            Construction userConstruction = memberAdmin.getConstruction();
            
            // 업무협의가 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
            if (workDiscussion.getConstruction() != null && 
                (userConstruction == null || !workDiscussion.getConstruction().getId().equals(userConstruction.getId()))) {
                return "redirect:/process/work-discussion/list?error=unauthorized";
            }
        }
        
        model.addAttribute("workDiscussion", workDiscussion);
        model.addAttribute("groups", DEFAULT_GROUPS);
        return "main/process/work-discussion/detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, 
                           @RequestParam(required = false) String groupName,
                           Model model, 
                           @AuthenticationPrincipal UserDetails userDetails) {
        WorkDiscussion workDiscussion = workDiscussionService.getWorkDiscussionById(id)
                .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));
        
        // 권한 체크: 매니저는 자신의 건설현장 업무협의만 수정 가능
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
            Construction userConstruction = memberAdmin.getConstruction();
            
            // 업무협의가 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
            if (workDiscussion.getConstruction() != null && 
                (userConstruction == null || !workDiscussion.getConstruction().getId().equals(userConstruction.getId()))) {
                return "redirect:/process/work-discussion/list?error=unauthorized";
            }
            
            model.addAttribute("construction", userConstruction);
            model.addAttribute("isAdmin", false);
        } else {
            model.addAttribute("isAdmin", true);
            model.addAttribute("constructions", constructionService.findAllConstructionsWithManagementMenus());
        }
        
        model.addAttribute("workDiscussion", workDiscussion);
        model.addAttribute("groups", DEFAULT_GROUPS);
        return "main/process/work-discussion/update";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, 
                        @ModelAttribute WorkDiscussion workDiscussion,
                        @RequestParam(value = "files", required = false) List<MultipartFile> files,
                        @RequestParam(value = "constructionId", required = false) Long constructionId,
                        @RequestParam(value = "mermaidCode", required = false) String mermaidCode,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            WorkDiscussion existingWorkDiscussion = workDiscussionService.getWorkDiscussionById(id)
                    .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));
            
            // 권한 체크: 매니저는 자신의 건설현장 업무협의만 수정 가능
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction userConstruction = memberAdmin.getConstruction();
                
                // 업무협의가 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
                if (existingWorkDiscussion.getConstruction() != null && 
                    (userConstruction == null || !existingWorkDiscussion.getConstruction().getId().equals(userConstruction.getId()))) {
                    return "redirect:/process/work-discussion/list?error=unauthorized";
                }
                
                // 관리자는 건설현장 변경 가능
                if (constructionId != null) {
                    Construction construction = constructionService.getConstructionWithManagementMenus(constructionId);
                    workDiscussion.setConstruction(construction);
                } else {
                    workDiscussion.setConstruction(null); // 전체 업무협의로 설정
                }
            } else {
                // 관리자는 건설현장 변경 가능
                if (constructionId != null) {
                    Construction construction = constructionService.getConstructionWithManagementMenus(constructionId);
                    workDiscussion.setConstruction(construction);
                } else {
                    workDiscussion.setConstruction(null); // 전체 업무협의로 설정
                }
            }
            
            // 머메이드 코드 설정
            workDiscussion.setMermaidCode(mermaidCode);
            
            // 그룹명이 없으면 기본값으로 설정
            if (workDiscussion.getGroupName() == null || workDiscussion.getGroupName().isEmpty()) {
                workDiscussion.setGroupName(DEFAULT_GROUPS.get(0)); // 첫 번째 그룹을 기본값으로 사용
            }
            
            // 업무협의 업데이트
            workDiscussionService.updateWorkDiscussion(id, workDiscussion, files);
            
            redirectAttributes.addFlashAttribute("message", "업무협의가 성공적으로 수정되었습니다.");
            return "redirect:/process/work-discussion/detail/" + id;
        } catch (Exception e) {
            log.error("업무협의 수정 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "업무협의 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/process/work-discussion/update/" + id;
        }
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, 
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            WorkDiscussion workDiscussion = workDiscussionService.getWorkDiscussionById(id)
                    .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));
            
            // 권한 체크: 매니저는 자신의 건설현장 업무협의만 삭제 가능
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction userConstruction = memberAdmin.getConstruction();
                
                // 업무협의가 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
                if (workDiscussion.getConstruction() != null && 
                    (userConstruction == null || !workDiscussion.getConstruction().getId().equals(userConstruction.getId()))) {
                    return "redirect:/process/work-discussion/list?error=unauthorized";
                }
            }
            
            String groupName = workDiscussion.getGroupName();
            workDiscussionService.deleteWorkDiscussion(id);
            
            redirectAttributes.addFlashAttribute("message", "업무협의가 성공적으로 삭제되었습니다.");
            // URL 인코딩 처리
            String encodedGroupName = UriUtils.encode(groupName, StandardCharsets.UTF_8);
            return "redirect:/process/work-discussion/list?groupName=" + encodedGroupName;
        } catch (Exception e) {
            log.error("업무협의 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "업무협의 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/process/work-discussion/list";
        }
    }

    @PostMapping("/attachment/{workDiscussionId}/remove/{attachmentIndex}")
    public String removeAttachment(@PathVariable Long workDiscussionId, 
                                 @PathVariable int attachmentIndex,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            WorkDiscussion workDiscussion = workDiscussionService.getWorkDiscussionById(workDiscussionId)
                    .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));
            
            // 권한 체크: 매니저는 자신의 건설현장 업무협의의 첨부파일만 삭제 가능
            if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
                Construction userConstruction = memberAdmin.getConstruction();
                
                // 업무협의가 특정 건설현장에 속하고, 사용자의 건설현장과 다른 경우 접근 거부
                if (workDiscussion.getConstruction() != null && 
                    (userConstruction == null || !workDiscussion.getConstruction().getId().equals(userConstruction.getId()))) {
                    return "redirect:/process/work-discussion/list?error=unauthorized";
                }
            }
            
            workDiscussionService.removeAttachment(workDiscussionId, attachmentIndex);
            
            redirectAttributes.addFlashAttribute("message", "첨부파일이 성공적으로 삭제되었습니다.");
            return "redirect:/process/work-discussion/update/" + workDiscussionId;
        } catch (Exception e) {
            log.error("첨부파일 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "첨부파일 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/process/work-discussion/update/" + workDiscussionId;
        }
    }
}
