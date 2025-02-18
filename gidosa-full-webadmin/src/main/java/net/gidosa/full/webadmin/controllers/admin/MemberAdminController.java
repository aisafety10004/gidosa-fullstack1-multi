package net.gidosa.full.webadmin.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.MemberAdminService;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/member/admin")
public class MemberAdminController {

    private final MemberAdminService memberAdminService;

    // 관리자 목록 조회 - 페이징 처리 추가
    @GetMapping("/list")
    public String list(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                      Model model) {
        Page<MemberAdmin> membersPage = memberAdminService.getAllMembersWithPaging(pageable);
        model.addAttribute("members", membersPage);
        return "main/member/admin/list";
    }

    // 관리자 상세 조회
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        memberAdminService.getMemberById(id)
                .ifPresent(member -> model.addAttribute("member", member));
        return "main/member/admin/detail";
    }

    // 관리자 정보 수정 폼
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        memberAdminService.getMemberById(id)
                .ifPresent(member -> model.addAttribute("member", member));
        return "main/member/admin/update";
    }

    // 관리자 정보 수정 처리
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute MemberAdmin memberDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            memberAdminService.updateMember(id, memberDetails);
            redirectAttributes.addFlashAttribute("message", "관리자 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "관리자 정보 수정 중 오류가 발생했습니다.");
            log.error("Error updating admin member", e);
        }
        return "redirect:/member/admin/detail/" + id;
    }

    // 관리자 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberAdminService.deleteMember(id);
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
            memberAdminService.toggleMemberStatus(id);
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
            memberAdminService.updateMemberRole(id, role);
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
        model.addAttribute("member", new MemberAdmin());
        return "main/member/admin/register";
    }

    // 관리자 등록 처리
    @PostMapping("/register")
    public String register(@ModelAttribute MemberAdmin memberAdmin, RedirectAttributes redirectAttributes) {
        try {
            // 사용자명 중복 체크
            if (memberAdminService.existsByUsername(memberAdmin.getUsername())) {
                redirectAttributes.addFlashAttribute("error", "이미 사용 중인 사용자명입니다.");
                return "redirect:/member/admin/register";
            }
            
            // 이메일 중복 체크
            if (memberAdminService.existsByEmail(memberAdmin.getEmail())) {
                redirectAttributes.addFlashAttribute("error", "이미 사용 중인 이메일입니다.");
                return "redirect:/member/admin/register";
            }

            memberAdminService.registerMember(memberAdmin);
            redirectAttributes.addFlashAttribute("message", "관리자가 성공적으로 등록되었습니다.");
            return "redirect:/member/admin/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "관리자 등록 중 오류가 발생했습니다.");
            log.error("Error registering admin member", e);
            return "redirect:/member/admin/register";
        }
    }
}
