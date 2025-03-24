package net.gidosa.full.webadmin.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webadmin.services.InquiryAdminService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.InquiryAdmin;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/inquiry")
@RequiredArgsConstructor
public class InquiryAdminController {

    private final InquiryAdminService inquiryAdminService;
    private final String FILE_UPLOAD_PATH = "uploads/inquiry-admin/";

    // 어드민 문의사항 목록 페이지
    @GetMapping("/admin/list")
    public String listAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchTitle,
            @RequestParam(required = false) String searchDateRange,
            @RequestParam(required = false) String inquiryType,
            @RequestParam(required = false) Boolean answered,
            @RequestParam(defaultValue = "id,desc") String sort,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        // 정렬 조건 설정
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // 검색 조건에 따라 문의사항 조회
        Page<InquiryAdmin> inquiries = inquiryAdminService.searchInquiries(null, searchTitle, searchDateRange, inquiryType, answered, pageable);

        model.addAttribute("inquiries", inquiries);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiries.getTotalPages());
        model.addAttribute("totalItems", inquiries.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("searchTitle", searchTitle);
        model.addAttribute("searchDateRange", searchDateRange);
        model.addAttribute("inquiryType", inquiryType);
        model.addAttribute("answered", answered);
        model.addAttribute("sort", sort);

        // 페이지 사이즈 선택 옵션 (10, 20, 30)
        List<Integer> pageSizes = List.of(10, 20, 30);
        model.addAttribute("pageSizes", pageSizes);

        return "main/inquiry/admin/list";
    }

    // 매니저 문의사항 목록 페이지
    @GetMapping("/list")
    public String list(
//            @RequestParam(required = false) Long constructionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchTitle,
            @RequestParam(required = false) String searchDateRange,
            @RequestParam(required = false) String inquiryType,
            @RequestParam(required = false) Boolean answered,
            @RequestParam(defaultValue = "id,desc") String sort,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();

        if (Objects.isNull(construction)) {
            return "redirect:/construction/select";
        }
        Long constructionId = construction.getId();

        // 정렬 조건 설정
        String[] sortParams = sort.split(",");
        String sortField = sortParams[0];
        String sortDirection = sortParams.length > 1 ? sortParams[1] : "desc";
        Sort.Direction direction = sortDirection.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        // 검색 조건에 따라 문의사항 조회
        Page<InquiryAdmin> inquiries = inquiryAdminService.searchInquiries(constructionId, searchTitle, searchDateRange, inquiryType, answered, pageable);

        model.addAttribute("inquiries", inquiries);
        model.addAttribute("constructionId", constructionId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inquiries.getTotalPages());
        model.addAttribute("totalItems", inquiries.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("searchTitle", searchTitle);
        model.addAttribute("searchDateRange", searchDateRange);
        model.addAttribute("inquiryType", inquiryType);
        model.addAttribute("answered", answered);
        model.addAttribute("sort", sort);

        // 페이지 사이즈 선택 옵션 (10, 20, 30)
        List<Integer> pageSizes = List.of(10, 20, 30);
        model.addAttribute("pageSizes", pageSizes);

        return "main/inquiry/list";
    }

    // 문의사항 상세 페이지
    @GetMapping("/detail/{id}")
    public String detail(
            @PathVariable Long id,
            @RequestParam Long constructionId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {
        MemberAdmin memberAdmin = ((net.gidosa.full.webadmin.configs.auth.PrincipalDetails) userDetails).getMemberAdmin();
        boolean isAdmin = memberAdmin.getRole().equals("ROLE_ADMIN");

        InquiryAdmin inquiryAdmin = inquiryAdminService.getInquiryById(id);
        model.addAttribute("inquiryAdmin", inquiryAdmin);
        model.addAttribute("constructionId", constructionId);
        model.addAttribute("isAdmin", isAdmin);
        
        return "main/inquiry/detail";
    }

    // 문의사항 등록 페이지
    @GetMapping("/create")
    public String createForm(
            @RequestParam Long constructionId,
            Model model
    ) {
        model.addAttribute("inquiryAdmin", new InquiryAdmin());
        model.addAttribute("constructionId", constructionId);
        return "main/inquiry/create";
    }

    // 문의사항 등록 처리
    @PostMapping("/create")
    public String create(
            @RequestParam Long constructionId,
            @ModelAttribute InquiryAdmin inquiryAdmin,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inquiryAdminService.createInquiry(constructionId, inquiryAdmin, files);
            redirectAttributes.addFlashAttribute("successMessage", "문의사항이 성공적으로 등록되었습니다.");
            return "redirect:/inquiry/list?constructionId=" + constructionId;
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/inquiry/create?constructionId=" + constructionId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "문의사항 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/inquiry/create?constructionId=" + constructionId;
        }
    }

    // 문의사항 수정 페이지
    @GetMapping("/edit/{id}")
    public String editForm(
            @PathVariable Long id,
            @RequestParam Long constructionId,
            Model model
    ) {
        InquiryAdmin inquiryAdmin = inquiryAdminService.getInquiryById(id);
        model.addAttribute("inquiryAdmin", inquiryAdmin);
        model.addAttribute("constructionId", constructionId);
        return "main/inquiry/edit";
    }

    // 문의사항 수정 처리
    @PostMapping("/edit/{id}")
    public String edit(
            @PathVariable Long id,
            @RequestParam Long constructionId,
            @ModelAttribute InquiryAdmin inquiryAdmin,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inquiryAdminService.updateInquiry(id, inquiryAdmin, files);
            redirectAttributes.addFlashAttribute("successMessage", "문의사항이 성공적으로 수정되었습니다.");
            return "redirect:/inquiry/detail/" + id + "?constructionId=" + constructionId;
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "파일 업로드 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/inquiry/edit/" + id + "?constructionId=" + constructionId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "문의사항 수정 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/inquiry/edit/" + id + "?constructionId=" + constructionId;
        }
    }

    // 문의사항 삭제 처리
    @PostMapping("/delete/{id}")
    public String delete(
            @PathVariable Long id,
            @RequestParam Long constructionId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inquiryAdminService.deleteInquiry(id);
            redirectAttributes.addFlashAttribute("successMessage", "문의사항이 성공적으로 삭제되었습니다.");
            return "redirect:/inquiry/list?constructionId=" + constructionId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "문의사항 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/inquiry/detail/" + id + "?constructionId=" + constructionId;
        }
    }

    // 문의사항 답변 처리
    @PostMapping("/answer/{id}")
    public String answer(
            @PathVariable Long id,
            @RequestParam Long constructionId,
            @RequestParam String answerContent,
            RedirectAttributes redirectAttributes
    ) {
        try {
            inquiryAdminService.answerInquiry(id, answerContent);
            redirectAttributes.addFlashAttribute("successMessage", "답변이 성공적으로 등록되었습니다.");
            return "redirect:/inquiry/detail/" + id + "?constructionId=" + constructionId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "답변 등록 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/inquiry/detail/" + id + "?constructionId=" + constructionId;
        }
    }

    // 파일 다운로드
    @GetMapping("/download/{id}/{fileIndex}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long id,
            @PathVariable int fileIndex,
            HttpServletRequest request
    ) {
        try {
            InquiryAdmin inquiryAdmin = inquiryAdminService.getInquiryById(id);
            
            // 파일 인덱스에 따라 첨부 파일 선택
            String originalFilename = "";
            String storedFilename = "";
            String contentType = "";
            Path filePath = null;
            
            if (fileIndex == 1 && inquiryAdmin.getFileAttachment1() != null) {
                originalFilename = inquiryAdmin.getFileAttachment1().getOriginalFilename();
                storedFilename = inquiryAdmin.getFileAttachment1().getStoredFilename();
                contentType = inquiryAdmin.getFileAttachment1().getContentType();
                filePath = Paths.get(inquiryAdmin.getFileAttachment1().getFilePath());
            } else if (fileIndex == 2 && inquiryAdmin.getFileAttachment2() != null) {
                originalFilename = inquiryAdmin.getFileAttachment2().getOriginalFilename();
                storedFilename = inquiryAdmin.getFileAttachment2().getStoredFilename();
                contentType = inquiryAdmin.getFileAttachment2().getContentType();
                filePath = Paths.get(inquiryAdmin.getFileAttachment2().getFilePath());
            } else if (fileIndex == 3 && inquiryAdmin.getFileAttachment3() != null) {
                originalFilename = inquiryAdmin.getFileAttachment3().getOriginalFilename();
                storedFilename = inquiryAdmin.getFileAttachment3().getStoredFilename();
                contentType = inquiryAdmin.getFileAttachment3().getContentType();
                filePath = Paths.get(inquiryAdmin.getFileAttachment3().getFilePath());
            } else {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = new UrlResource(filePath.toUri());
            
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }
            
            // 다운로드 헤더 설정
            String encodedFilename = new String(originalFilename.getBytes("UTF-8"), "ISO-8859-1");
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
