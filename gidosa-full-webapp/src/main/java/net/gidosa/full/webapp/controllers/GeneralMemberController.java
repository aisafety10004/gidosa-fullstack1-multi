package net.gidosa.full.webapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.models.dtos.MemberRegisterDto;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import net.gidosa.full.webapp.services.GeneralMemberService;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/general/member")
public class GeneralMemberController {
    private final GeneralMemberService generalMemberService;
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;

    @GetMapping("/register/agreement")
    public String registerAgreement(@RequestParam(value = "constructionId") Long constructionId, Model model) {
        model.addAttribute("headerInvisible", true);
        model.addAttribute("headerSubInvisible", true);
        model.addAttribute("constructionId", constructionId);
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/member/register-agreement";
    }

    @GetMapping("/register")
    public String registerForm(@RequestParam(value = "constructionId") Long constructionId, Model model) {
//        model.addAttribute("constructionId", constructionId);
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        model.addAttribute("headerSubInvisible", true);
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/member/register";
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "회원 가입 처리 중 오류가 발생했습니다. " + e.getMessage());
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

        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

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
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/member/find-pw";
    }

    @PostMapping(value = "/async-find-pw", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> findPwApi(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        String email = (String) request.get("email");
        Long constructionId = Long.valueOf(request.get("constructionId").toString());
        
        Construction construction = generalConstructionService.getConstruction(constructionId);
        boolean sent = generalMemberService.processFindPassword(username, email, construction);
        
        return Map.of(
            "sent", sent,
            "message1", sent ? "임시 비밀번호가 이메일로 발송되었습니다." : "일치하는 회원 정보를 찾을 수 없습니다.",
            "message2", sent ? "(이메일 주소 확인요망(스팸함 포함))" : ""
        );
    }
}