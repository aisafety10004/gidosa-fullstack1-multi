package net.gidosa.full.webadmin.controllers.managers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.MemberGeneralService;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.data.web.PageableDefault;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/member/general")
public class MemberGeneralController {

    private final MemberGeneralService memberGeneralService;

    // 회원 목록 조회 - 페이징 처리 추가
    @GetMapping("/list")
    public String list(@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                      Model model) {
        Page<MemberGeneral> membersPage = memberGeneralService.getAllMembersWithPaging(pageable);
        model.addAttribute("members", membersPage);
        return "main/member/general/list";
    }

    // 회원 상세 조회
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        memberGeneralService.getMemberById(id)
                .ifPresent(member -> model.addAttribute("member", member));
        return "main/member/general/detail";
    }

    // 회원 정보 수정 폼
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        memberGeneralService.getMemberById(id)
                .ifPresent(member -> model.addAttribute("member", member));
        return "main/member/general/update";
    }

    // 회원 정보 수정 처리
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute MemberGeneral memberDetails,
                        RedirectAttributes redirectAttributes) {
        try {
            memberGeneralService.updateMember(id, memberDetails);
            redirectAttributes.addFlashAttribute("message", "회원 정보가 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 정보 수정 중 오류가 발생했습니다.");
            log.error("Error updating member", e);
        }
        return "redirect:/member/general/detail/" + id;
    }

    // 회원 삭제
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            memberGeneralService.deleteMember(id);
            redirectAttributes.addFlashAttribute("message", "회원이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원 삭제 중 오류가 발생했습니다.");
            log.error("Error deleting member", e);
        }
        return "redirect:/member/general/list";
    }
}
