package net.gidosa.full.webapp.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {

    @GetMapping("/request-site")
    public String showContactForm(Model model) {
        return "pages/contact/request-site";
    }

    @PostMapping("/submit")
    public String submitContactForm(ContactRequest request, RedirectAttributes redirectAttributes) {
        // TODO: 실제 문의 처리 로직 구현
        redirectAttributes.addFlashAttribute("message", "문의가 성공적으로 접수되었습니다.");
        return "redirect:/contact/request-site";
    }

    @GetMapping("/example")
    public String showContactExample(Model model) {
        // 샘플 건물 데이터 추가
        List<Building> buildings = Arrays.asList(
            new Building("A 건설공사", "/images/building1.jpg"),
            new Building("B 건설공사", "/images/building2.jpg"),
            new Building("C 건설공사", "/images/building3.jpg")
        );
        
        model.addAttribute("buildings", buildings);
        return "pages/contact/example";
    }
}

record ContactRequest(
    String name,
    String phone,
    String churchName,
    String position,
    String message,
    boolean agreement
) {}

// Building 클래스
@Data
@AllArgsConstructor
class Building {
    private String name;
    private String imageUrl;
} 