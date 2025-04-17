package net.gidosa.full.webadmin.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.models.dtos.CustomHtmlPageDTO;
import net.gidosa.full.webadmin.services.CustomHtmlPageService;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomHtmlPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/settings/html-manager")
public class CustomHtmlManagerController {
    private final CustomHtmlPageService customHtmlPageService;
    
    @GetMapping
    public String list(@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                      @RequestParam(required = false) String searchTitle,
                      @RequestParam(required = false) String startDate,
                      @RequestParam(required = false) String endDate,
                      @RequestParam(required = false) Boolean published,
                      @RequestParam(required = false) String sort,
                      @RequestParam(required = false, defaultValue = "desc") String direction,
                      @RequestParam(required = false) String viewMode,
                      Model model,
                      @AuthenticationPrincipal UserDetails userDetails) {
        
        // 정렬 처리
        if (sort != null && !sort.isEmpty()) {
            Sort sortObj = direction.equalsIgnoreCase("desc") ? 
                Sort.by(Sort.Direction.DESC, sort) : 
                Sort.by(Sort.Direction.ASC, sort);
            pageable = PageRequest.of(
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
        model.addAttribute("published", published);
        model.addAttribute("viewMode", viewMode);
        
        // 정렬 파라미터를 모델에 추가
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        
        // 사용자 권한 및 소속 공사 정보 가져오기
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        Long constructionId = null;
        if (!isAdmin && userDetails instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) userDetails;
            if (principalDetails.getMemberAdmin() != null && principalDetails.getMemberAdmin().getConstruction() != null) {
                constructionId = principalDetails.getMemberAdmin().getConstruction().getId();
            }
        }
        
        // 검색 조건에 따른 조회
        Page<CustomHtmlPage> htmlPages;
        
        // 관리자인 경우 뷰 모드에 따라 다른 검색 결과 제공
        if (isAdmin) {
            if ("noConstruction".equals(viewMode)) {
                // 전체 공통 HTML 페이지만 조회 (construction이 null인 경우)
                htmlPages = searchByConditionsForNoConstruction(searchTitle, startDateTime, endDateTime, published, pageable);
            } else {
                // 모든 HTML 페이지 조회 (기본 viewMode = 'all')
                htmlPages = searchByConditionsForAllPages(searchTitle, startDateTime, endDateTime, published, pageable);
            }
        } else {
            // 매니저인 경우 자신의 공사 소속 HTML 페이지와 공통 HTML 페이지만 조회
            if (constructionId != null) {
                htmlPages = searchByConditionsForManagerWithConstruction(searchTitle, startDateTime, endDateTime, published, constructionId, pageable);
            } else {
                // 매니저이지만 공사 소속이 없는 경우 공통 HTML 페이지만 조회
                htmlPages = searchByConditionsForNoConstruction(searchTitle, startDateTime, endDateTime, published, pageable);
            }
        }
        
        model.addAttribute("htmlPages", htmlPages);
        return "main/settings/html-manager/list";
    }
    
    // 모든 HTML 페이지 검색 (관리자용)
    private Page<CustomHtmlPage> searchByConditionsForAllPages(String searchTitle, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean published, Pageable pageable) {
        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            if (startDateTime != null || endDateTime != null) {
                if (published != null) {
                    // 제목 + 날짜 + 게시상태
                    return customHtmlPageService.searchHtmlPagesByTitleAndDateRangeAndPublished(
                            searchTitle, startDateTime, endDateTime, published, pageable);
                } else {
                    // 제목 + 날짜
                    return customHtmlPageService.searchHtmlPagesByTitleAndDateRange(
                            searchTitle, startDateTime, endDateTime, pageable);
                }
            } else if (published != null) {
                // 제목 + 게시상태
                return customHtmlPageService.searchHtmlPagesByTitleAndPublished(searchTitle, published, pageable);
            } else {
                // 제목만
                return customHtmlPageService.searchHtmlPagesByTitle(searchTitle, pageable);
            }
        } else if (startDateTime != null || endDateTime != null) {
            if (published != null) {
                // 날짜 + 게시상태
                return customHtmlPageService.searchHtmlPagesByDateRangeAndPublished(
                        startDateTime, endDateTime, published, pageable);
            } else {
                // 날짜만
                return customHtmlPageService.searchHtmlPagesByDateRange(startDateTime, endDateTime, pageable);
            }
        } else if (published != null) {
            // 게시상태만
            return customHtmlPageService.searchHtmlPagesByPublished(published, pageable);
        } else {
            // 조건없음 전체 조회
            return customHtmlPageService.getAllHtmlPages(pageable);
        }
    }
    
    // construction이 null인 HTML 페이지만 검색 (공통 HTML 페이지)
    private Page<CustomHtmlPage> searchByConditionsForNoConstruction(String searchTitle, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean published, Pageable pageable) {
        // 기본적으로 construction이 null인 페이지만 필터링
        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            // 제목 + construction is null
            return customHtmlPageService.searchHtmlPagesByTitleAndNoConstruction(searchTitle, pageable);
        } else {
            // construction is null만
            return customHtmlPageService.getHtmlPagesWithNoConstruction(pageable);
        }
    }
    
    // 매니저용 HTML 페이지 검색 (본인 소속 공사 + 공통 HTML 페이지)
    private Page<CustomHtmlPage> searchByConditionsForManagerWithConstruction(String searchTitle, LocalDateTime startDateTime, LocalDateTime endDateTime, Boolean published, Long constructionId, Pageable pageable) {
        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            // 제목 + construction id
            return customHtmlPageService.searchHtmlPagesByTitleAndConstructionId(searchTitle, constructionId, pageable);
        } else if (startDateTime != null || endDateTime != null) {
            // 날짜범위 + construction id
            return customHtmlPageService.searchHtmlPagesByDateRangeAndConstructionId(startDateTime, endDateTime, constructionId, pageable);
        } else if (published != null) {
            // 게시상태 + construction id
            return customHtmlPageService.searchHtmlPagesByPublishedAndConstructionId(published, constructionId, pageable);
        } else {
            // construction id만
            return customHtmlPageService.getHtmlPagesByConstructionId(constructionId, pageable);
        }
    }
    
    @GetMapping("/create")
    public String createForm(Model model) {
        // 빈 DTO 객체를 모델에 추가
        model.addAttribute("htmlPageDTO", new CustomHtmlPageDTO());
        return "main/settings/html-manager/create";
    }
    
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("htmlPageDTO") CustomHtmlPageDTO htmlPageDTO,
                        BindingResult bindingResult,
                        @RequestParam(value = "htmlFile", required = false) MultipartFile htmlFile,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        
        // 유효성 검증 실패 시 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            // 에러 메시지를 모델에 추가
            log.error("유효성 검증 실패: {}", bindingResult.getAllErrors());
            return "main/settings/html-manager/create";
        }
        
        try {
            // 사용자 권한 확인 및 construction 정보 설정
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isAdmin && userDetails instanceof PrincipalDetails) {
                PrincipalDetails principalDetails = (PrincipalDetails) userDetails;
                if (principalDetails.getMemberAdmin() != null && principalDetails.getMemberAdmin().getConstruction() != null) {
                    // 매니저인 경우 자신의 소속 공사 ID로 설정
                    htmlPageDTO.setConstructionId(principalDetails.getMemberAdmin().getConstruction().getId());
                    htmlPageDTO.setConstructionName(principalDetails.getMemberAdmin().getConstruction().getName());
                }
            }
            
            // DTO를 엔티티로 변환하여 저장
            CustomHtmlPage createdPage = customHtmlPageService.createHtmlPageFromDTO(htmlPageDTO, htmlFile);
            redirectAttributes.addFlashAttribute("successMessage", "HTML 페이지가 생성되었습니다.");
            return "redirect:/settings/html-manager";
        } catch (Exception e) {
            log.error("HTML 페이지 생성 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "HTML 페이지 생성 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/settings/html-manager/create";
        }
    }
    
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<CustomHtmlPage> htmlPageOpt = customHtmlPageService.getHtmlPageById(id);
        
        if (htmlPageOpt.isPresent()) {
            CustomHtmlPage htmlPage = htmlPageOpt.get();
            
            // 접근 권한 확인
            boolean hasAccess = checkAccess(htmlPage, userDetails);
            if (!hasAccess) {
                return "redirect:/settings/html-manager";
            }
            
            model.addAttribute("htmlPage", htmlPage);
            return "main/settings/html-manager/detail";
        } else {
            return "redirect:/settings/html-manager";
        }
    }
    
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Optional<CustomHtmlPage> htmlPageOpt = customHtmlPageService.getHtmlPageById(id);
        
        if (htmlPageOpt.isPresent()) {
            CustomHtmlPage htmlPage = htmlPageOpt.get();
            
            // 접근 권한 확인
            boolean hasAccess = checkAccess(htmlPage, userDetails);
            if (!hasAccess) {
                return "redirect:/settings/html-manager";
            }
            
            // 엔티티를 DTO로 변환
            CustomHtmlPageDTO htmlPageDTO = CustomHtmlPageDTO.builder()
                    .id(htmlPage.getId())
                    .title(htmlPage.getTitle())
                    .content(htmlPage.getContent())
                    .published(htmlPage.getPublished())
                    .build();
            
            // construction 정보 설정
            if (htmlPage.getConstruction() != null) {
                htmlPageDTO.setConstructionId(htmlPage.getConstruction().getId());
                htmlPageDTO.setConstructionName(htmlPage.getConstruction().getName());
            }
            
            model.addAttribute("htmlPageDTO", htmlPageDTO);
            model.addAttribute("htmlPage", htmlPage); // 기존 파일 정보 등을 표시하기 위해 엔티티도 추가
            return "main/settings/html-manager/update";
        } else {
            return "redirect:/settings/html-manager";
        }
    }
    
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                        @Valid @ModelAttribute("htmlPageDTO") CustomHtmlPageDTO htmlPageDTO,
                        BindingResult bindingResult,
                        @RequestParam(value = "htmlFile", required = false) MultipartFile htmlFile,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        
        // 유효성 검증 실패 시 폼으로 다시 이동
        if (bindingResult.hasErrors()) {
            // 기존 HTML 파일 정보를 모델에 다시 추가
            customHtmlPageService.getHtmlPageById(id).ifPresent(htmlPage -> {
                model.addAttribute("htmlPage", htmlPage);
            });
            
            log.error("유효성 검증 실패: {}", bindingResult.getAllErrors());
            return "main/settings/html-manager/update";
        }
        
        // 접근 권한 확인
        Optional<CustomHtmlPage> htmlPageOpt = customHtmlPageService.getHtmlPageById(id);
        if (htmlPageOpt.isEmpty() || !checkAccess(htmlPageOpt.get(), userDetails)) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 HTML 페이지에 접근할 권한이 없습니다.");
            return "redirect:/settings/html-manager";
        }
        
        try {
            // 사용자 권한 확인 및 construction 정보 설정
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            
            if (!isAdmin && userDetails instanceof PrincipalDetails) {
                PrincipalDetails principalDetails = (PrincipalDetails) userDetails;
                if (principalDetails.getMemberAdmin() != null && principalDetails.getMemberAdmin().getConstruction() != null) {
                    // 매니저인 경우 자신의 소속 공사 ID로 설정
                    htmlPageDTO.setConstructionId(principalDetails.getMemberAdmin().getConstruction().getId());
                    htmlPageDTO.setConstructionName(principalDetails.getMemberAdmin().getConstruction().getName());
                }
            }
            
            // DTO를 엔티티로 변환하여 업데이트
            CustomHtmlPage updatedHtmlPage = customHtmlPageService.updateHtmlPageFromDTO(id, htmlPageDTO, htmlFile);
            redirectAttributes.addFlashAttribute("successMessage", "HTML 페이지가 수정되었습니다.");
            return "redirect:/settings/html-manager/detail/" + id;
        } catch (Exception e) {
            log.error("HTML 페이지 수정 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "HTML 페이지 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/settings/html-manager/update/" + id;
        }
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                        @AuthenticationPrincipal UserDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        // 접근 권한 확인
        Optional<CustomHtmlPage> htmlPageOpt = customHtmlPageService.getHtmlPageById(id);
        if (htmlPageOpt.isEmpty() || !checkAccess(htmlPageOpt.get(), userDetails)) {
            redirectAttributes.addFlashAttribute("errorMessage", "해당 HTML 페이지에 접근할 권한이 없습니다.");
            return "redirect:/settings/html-manager";
        }
        
        try {
            customHtmlPageService.deleteHtmlPage(id);
            redirectAttributes.addFlashAttribute("successMessage", "HTML 페이지가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("HTML 페이지 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "HTML 페이지 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/settings/html-manager";
    }
    
    // HTML 페이지에 대한 접근 권한 확인 메서드
    private boolean checkAccess(CustomHtmlPage htmlPage, UserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        
        // 관리자는 모든 페이지에 접근 가능
        if (isAdmin) {
            return true;
        }
        
        // 매니저의 경우 자신의 공사에 속한
        if (userDetails instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) userDetails;
            if (principalDetails.getMemberAdmin() != null && principalDetails.getMemberAdmin().getConstruction() != null) {
                Long managerConstructionId = principalDetails.getMemberAdmin().getConstruction().getId();
                
                // 공통 HTML 페이지(construction = null)이거나 자신의 공사 페이지인 경우 접근 가능
                return htmlPage.getConstruction() == null || 
                       (htmlPage.getConstruction() != null && 
                        htmlPage.getConstruction().getId().equals(managerConstructionId));
            }
        }
        
        return false;
    }
    
    @PostMapping("/publish/{id}")
    public String publishHtmlPage(@PathVariable Long id,
                                @RequestParam(defaultValue = "true") boolean publish,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            customHtmlPageService.updatePublishStatus(id, publish);
            String message = publish ? "HTML 페이지가 게시되었습니다." : "HTML 페이지가 게시 해제되었습니다.";
            redirectAttributes.addFlashAttribute("successMessage", message);
        } catch (Exception e) {
            log.error("HTML 페이지 게시 상태 변경 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "HTML 페이지 게시 상태 변경 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/settings/html-manager/detail/" + id;
    }
    
    @PostMapping("/html-file/{id}/remove")
    public String removeHtmlFile(@PathVariable Long id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               RedirectAttributes redirectAttributes) {
        try {
            customHtmlPageService.removeHtmlFile(id);
            redirectAttributes.addFlashAttribute("successMessage", "HTML 파일이 제거되었습니다.");
        } catch (Exception e) {
            log.error("HTML 파일 제거 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "HTML 파일 제거 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/settings/html-manager/update/" + id;
    }
}
