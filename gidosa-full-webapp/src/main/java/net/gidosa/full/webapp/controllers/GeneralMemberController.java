package net.gidosa.full.webapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.models.dtos.MemberRegisterDto;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import net.gidosa.full.webapp.services.GeneralMemberService;

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
        model.addAttribute("memberRegisterDto", new MemberRegisterDto());

        return "pages/general/member/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute MemberRegisterDto memberRegisterDto,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "pages/general/member/register?constructionId=" + memberRegisterDto.getConstructionId();
        }

        try {
            generalMemberService.register(memberRegisterDto);
            return "redirect:/general/auth/login?constructionId=" + memberRegisterDto.getConstructionId() + "&registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/general/member/register?constructionId=" +  + memberRegisterDto.getConstructionId();
        }
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(String username) {
        return true;
    }

    @GetMapping("/find-id")
    public String findIdForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
        model.addAttribute("headerSubInvisible", true);
        model.addAttribute("constructionId", constructionId);

        return "pages/general/member/find-id";
    }

    @PostMapping("/find-id")
    public String findId(String name, String email, Model model) {
        // TODO: Implement find ID logic
        boolean found = false;
        String message = "일치하는 회원 정보를 찾을 수 없습니다.";

        // 임시 로직 (실제로는 서비스 계층에서 처리)
        if ("홍길동".equals(name) && "test@example.com".equals(email)) {
            found = true;
            message = "회원님의 아이디는 'hong123' 입니다.";
        }

        model.addAttribute("found", found);
        model.addAttribute("message", message);
        return "pages/general/member/find-id";
    }

    @GetMapping("/find-pw")
    public String findPwForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
        model.addAttribute("headerSubInvisible", true);
        model.addAttribute("constructionId", constructionId);

        return "pages/general/member/find-pw";
    }

    @PostMapping("/find-pw")
    public String findPw(String username, String email, Model model, RedirectAttributes redirectAttributes) {
        // TODO: Implement find password logic
        boolean sent = false;
        String message = "일치하는 회원 정보를 찾을 수 없습니다.";

        // 임시 로직 (실제로는 서비스 계층에서 처리)
        if ("hong123".equals(username) && "test@example.com".equals(email)) {
            sent = true;
            message = "임시 비밀번호가 이메일로 발송되었습니다.";
            // TODO: 실제 이메일 발송 로직 구현
        }

        model.addAttribute("sent", sent);
        model.addAttribute("message", message);
        return "pages/general/member/find-pw";
    }
}