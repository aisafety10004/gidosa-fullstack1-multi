package net.gidosa.full.webapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.dto.MemberRegisterDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import net.gidosa.full.webapp.services.MemberService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/register/agreement")
    public String registerAgreement(Model model) {
        model.addAttribute("headerInvisible", true);

        return "pages/member/register-agreement";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("memberRegisterDto", new MemberRegisterDto());
        return "pages/member/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute MemberRegisterDto memberRegisterDto,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            return "pages/member/register";
        }

        try {
            memberService.register(memberRegisterDto);
            return "redirect:/auth/login?registered";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "pages/member/register";
        }
    }

    @GetMapping("/check-username")
    @ResponseBody
    public boolean checkUsername(String username) {
        return true;
    }

    @GetMapping("/find-id")
    public String findIdForm(Model model) {
        return "pages/member/find-id";
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
        return "pages/member/find-id";
    }

    @GetMapping("/find-pw")
    public String findPwForm(Model model) {
        return "pages/member/find-pw";
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
        return "pages/member/find-pw";
    }
}