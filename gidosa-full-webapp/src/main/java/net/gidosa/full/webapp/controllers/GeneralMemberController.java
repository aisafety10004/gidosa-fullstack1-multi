package net.gidosa.full.webapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.models.dtos.MemberRegisterDto;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import net.gidosa.full.webapp.services.GeneralMemberService;

import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/general/member")
public class GeneralMemberController {
    private final GeneralMemberService generalMemberService;
    private final GeneralConstructionService generalConstructionService;

    @GetMapping("/register/agreement")
    public String registerAgreement(@RequestParam(value = "constructionId") Long constructionId, Model model) {
        model.addAttribute("headerInvisible", true);
        model.addAttribute("headerSubInvisible", true);
        model.addAttribute("constructionId", constructionId);

        return "pages/general/member/register-agreement";
    }

    @GetMapping("/register")
    public String registerForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
//        model.addAttribute("constructionId", constructionId);
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);

        return "pages/general/member/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute MemberRegisterDto memberRegisterDto,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        Construction construction = generalConstructionService.getConstruction(memberRegisterDto.getConstructionId());
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);
        model.addAttribute("memberRegisterDto", memberRegisterDto);

        if (bindingResult.hasErrors()) {
            return "pages/general/member/register?constructionId=" + memberRegisterDto.getConstructionId();
        }

        try {
            MemberGeneral memberGeneral = generalMemberService.register(memberRegisterDto, construction);
            redirectAttributes.addFlashAttribute("construction", construction);
            redirectAttributes.addFlashAttribute("memberGeneral", memberGeneral);

            return "redirect:/general/auth/login?constructionId=" + memberRegisterDto.getConstructionId() + "&registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/general/member/register";
        }
    }

    @GetMapping("/check-username")
    @ResponseBody
    public Map<String, Boolean> checkUsername(@RequestParam String username) {
        boolean isAvailable = generalMemberService.isUsernameAvailable(username);
        return Map.of("available", isAvailable);
    }

    @GetMapping("/find-id")
    public String findIdForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);

        return "pages/general/member/find-id";
    }

    @PostMapping("/find-id")
    public String findId(String name, 
                        String email, 
                        @RequestParam(value = "constructionId") Long constructionId,
                        Model model) {
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);

        Optional<MemberGeneral> memberOpt = generalMemberService.findByNameAndEmailAndConstructionId(name, email, constructionId);
        
        boolean found = memberOpt.isPresent();
        String message = found 
            ? String.format("회원님의 아이디는 '%s' 입니다.", memberOpt.get().getUsername())
            : "일치하는 회원 정보를 찾을 수 없습니다.";

        model.addAttribute("found", found);
        model.addAttribute("message", message);
        
        return "pages/general/member/find-id";
    }

    @GetMapping("/find-pw")
    public String findPwForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);

        return "pages/general/member/find-pw";
    }

    @PostMapping("/find-pw")
    public String findPw(String username, 
                        String email, 
                        @RequestParam(value = "constructionId") Long constructionId,
                        Model model) {
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);
        
        boolean sent = generalMemberService.processFindPassword(username, email);
        String message1 = sent
            ? "임시 비밀번호가 이메일로 발송되었습니다."
            : "일치하는 회원 정보를 찾을 수 없습니다.";
        String message2 = sent
                ? "(이메일 주소 확인요망(스팸함 포함))"
                : "";
        
        model.addAttribute("sent", sent);
        model.addAttribute("message1", message1);
        model.addAttribute("message2", message2);
        
        return "pages/general/member/find-pw";
    }
}