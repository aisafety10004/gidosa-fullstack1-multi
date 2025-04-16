package net.gidosa.full.webadmin.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
        
        // 정렬 파라미터를 모델에 추가
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        
        // 검색 조건에 따른 조회
        Page<CustomHtmlPage> htmlPages;
        
        // 조건 조합에 따른 분기 처리
        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            if (startDateTime != null || endDateTime != null) {
                if (published != null) {
                    // 제목 + 날짜 + 게시상태
                    htmlPages = customHtmlPageService.searchHtmlPagesByTitleAndDateRangeAndPublished(
                            searchTitle, startDateTime, endDateTime, published, pageable);
                } else {
                    // 제목 + 날짜
                    htmlPages = customHtmlPageService.searchHtmlPagesByTitleAndDateRange(
                            searchTitle, startDateTime, endDateTime, pageable);
                }
            } else if (published != null) {
                // 제목 + 게시상태
                htmlPages = customHtmlPageService.searchHtmlPagesByTitleAndPublished(searchTitle, published, pageable);
            } else {
                // 제목만
                htmlPages = customHtmlPageService.searchHtmlPagesByTitle(searchTitle, pageable);
            }
        } else if (startDateTime != null || endDateTime != null) {
            if (published != null) {
                // 날짜 + 게시상태
                htmlPages = customHtmlPageService.searchHtmlPagesByDateRangeAndPublished(
                        startDateTime, endDateTime, published, pageable);
            } else {
                // 날짜만
                htmlPages = customHtmlPageService.searchHtmlPagesByDateRange(startDateTime, endDateTime, pageable);
            }
        } else if (published != null) {
            // 게시상태만
            htmlPages = customHtmlPageService.searchHtmlPagesByPublished(published, pageable);
        } else {
            // 조건없음 전체 조회
            htmlPages = customHtmlPageService.getAllHtmlPages(pageable);
        }
        
        model.addAttribute("htmlPages", htmlPages);
        return "main/settings/html-manager/list";
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
            model.addAttribute("htmlPage", htmlPageOpt.get());
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
            
            // 엔티티를 DTO로 변환
            CustomHtmlPageDTO htmlPageDTO = CustomHtmlPageDTO.builder()
                    .id(htmlPage.getId())
                    .title(htmlPage.getTitle())
                    .content(htmlPage.getContent())
                    .published(htmlPage.getPublished())
                    .build();
            
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
        
        try {
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
        try {
            customHtmlPageService.deleteHtmlPage(id);
            redirectAttributes.addFlashAttribute("successMessage", "HTML 페이지가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("HTML 페이지 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("errorMessage", "HTML 페이지 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/settings/html-manager";
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
