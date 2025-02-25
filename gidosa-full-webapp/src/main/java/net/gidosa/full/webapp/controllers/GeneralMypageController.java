package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import net.gidosa.full.webapp.dtos.MemberUpdateDto;
import net.gidosa.full.webapp.services.GeneralMemberService;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/general/mypage")
public class GeneralMypageController {

    private final GeneralMemberService generalMemberService;

    @GetMapping("/profile")
    public String profileForm(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        MemberUpdateDto memberUpdateDto = MemberUpdateDto.from(principalDetails.getMemberGeneral());
        model.addAttribute("memberUpdateDto", memberUpdateDto);
        return "pages/general/mypage/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal PrincipalDetails principalDetails,
                              @ModelAttribute MemberUpdateDto memberUpdateDto,
                              RedirectAttributes redirectAttributes) {
        try {
            generalMemberService.updateGeneralMember(principalDetails.getMemberGeneral().getId(), memberUpdateDto);

            MemberGeneral updatedMember = generalMemberService.getGeneralMemberByIdWithConstruction(principalDetails.getMemberGeneral().getId());
            principalDetails.setMemberGeneral(updatedMember);
            
            redirectAttributes.addFlashAttribute("message", "개인정보가 성공적으로 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "개인정보 수정 중 오류가 발생했습니다.");
        }
        return "redirect:/general/mypage/profile";
    }
} 