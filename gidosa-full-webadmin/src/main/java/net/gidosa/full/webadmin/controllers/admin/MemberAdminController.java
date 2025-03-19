package net.gidosa.full.webadmin.controllers.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.models.dtos.MemberAdminUpdateDto;
import net.gidosa.full.webadmin.services.MemberAdminService;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.full.webadmin.models.dtos.MemberAdminRegisterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/member/admin")
public class MemberAdminController {
    private final MemberAdminService memberAdminService;

    // 관리자 목록 조회 - 페이징 처리 추가
    @GetMapping("/list")
    public String list(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                      @RequestParam(required = false) String searchType,
                      @RequestParam(required = false) String keyword,
                      @RequestParam(required = false) String sort,
                      @RequestParam(required = false, defaultValue = "desc") String direction,
                      @RequestParam(required = false) Integer size,
                      Model model) {
        // 페이지 크기 처리
        if (size != null && (size == 10 || size == 20 || size == 30)) {
            pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        }
        
        // 정렬 처리
        if (sort != null && !sort.isEmpty()) {
            Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            pageable = PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(), 
                Sort.by(sortDirection, sort)
            );
        }
        
        Page<MemberAdmin> memberAdminsPage;
        
        if (searchType != null && keyword != null && !keyword.trim().isEmpty()) {
            memberAdminsPage = memberAdminService.searchMemberAdmins(searchType, keyword, pageable);
            model.addAttribute("searchType", searchType);
            model.addAttribute("keyword", keyword);
        } else {
            memberAdminsPage = memberAdminService.getAllMembersWithPaging(pageable);
            if (searchType != null) {
                model.addAttribute("searchType", searchType);
            }
        }
        
        model.addAttribute("memberAdminListPage", memberAdminsPage);
        return "main/member/admin/list";
    }

    // 관리자 상세 조회
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        memberAdminService.getMemberAdminById(id)
                .ifPresent(memberAdmin -> model.addAttribute("memberAdmin", memberAdmin));
        return "main/member/admin/detail";
    }

    // 관리자 정보 수정 폼
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        memberAdminService.getMemberAdminById(id)
                .ifPresent(memberAdmin -> {
                    model.addAttribute("memberAdmin", memberAdmin);
                    model.addAttribute("constructionId", Objects.isNull(memberAdmin.getConstruction()) ? "" : memberAdmin.getConstruction().getId());
                });
        model.addAttribute("constructions", memberAdminService.getAllConstructions());

        return "main/member/admin/update";
    }

    // 관리자 정보 수정 처리
    @PostMapping("/update/{id}")
    public String update(@Valid @ModelAttribute("memberAdmin") MemberAdminUpdateDto memberAdminUpdateDto,
//                         @PathVariable Long id,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("constructionId", memberAdminUpdateDto.getConstructionId());
            model.addAttribute("constructions", memberAdminService.getAllConstructions());
            return "main/member/admin/update";
        }

        try {
            memberAdminService.updateMemberAdmin(memberAdminUpdateDto);
            redirectAttributes.addFlashAttribute("message", "관리자 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "관리자 정보 수정 중 오류가 발생했습니다.");
            log.error("Error updating admin member", e);
        }
        return "redirect:/member/admin/detail/" + memberAdminUpdateDto.getId();
    }

    // 관리자 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberAdminService.deleteMemberAdmin(id);
            redirectAttributes.addFlashAttribute("message", "관리자가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "관리자 삭제 중 오류가 발생했습니다.");
            log.error("Error deleting admin member", e);
        }
        return "redirect:/member/admin/list";
    }

    // 관리자 계정 활성/비활성 토글
    @PostMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberAdminService.toggleMemberAdminStatus(id);
            redirectAttributes.addFlashAttribute("message", "계정 상태가 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "계정 상태 변경 중 오류가 발생했습니다.");
            log.error("Error toggling member status", e);
        }
        return "redirect:/member/admin/detail/" + id;
    }

    // 관리자 권한 변경
    @PostMapping("/update-role/{id}")
    public String updateRole(@PathVariable Long id, @RequestParam String role, 
                           RedirectAttributes redirectAttributes) {
        try {
            memberAdminService.updateMemberAdminRole(id, role);
            redirectAttributes.addFlashAttribute("message", "관리자 권한이 변경되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "권한 변경 중 오류가 발생했습니다.");
            log.error("Error updating member role", e);
        }
        return "redirect:/member/admin/detail/" + id;
    }

    // 관리자 등록 폼
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("memberAdminDto", new MemberAdminRegisterDto());
        model.addAttribute("constructions", memberAdminService.getAllConstructions());
        return "main/member/admin/register";
    }

    // 관리자 등록 처리
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("memberAdminDto") MemberAdminRegisterDto memberAdminRegisterDto,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("constructions", memberAdminService.getAllConstructions());
            return "main/member/admin/register";
        }

        try {
            // 사용자명 중복 체크
            if (memberAdminService.existsByUsername(memberAdminRegisterDto.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "이미 사용 중인 사용자명입니다.");
                return "redirect:/member/admin/register";
            }
            
            // 이메일 중복 체크
            if (memberAdminService.existsByEmail(memberAdminRegisterDto.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "이미 사용 중인 이메일입니다.");
                return "redirect:/member/admin/register";
            }

            memberAdminService.registerMemberAdmin(memberAdminRegisterDto);
            redirectAttributes.addFlashAttribute("message", "관리자가 성공적으로 등록되었습니다.");
            return "redirect:/member/admin/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "관리자 등록 중 오류가 발생했습니다.");
            log.error("Error registering admin member", e);
            return "redirect:/member/admin/register";
        }
    }
}
