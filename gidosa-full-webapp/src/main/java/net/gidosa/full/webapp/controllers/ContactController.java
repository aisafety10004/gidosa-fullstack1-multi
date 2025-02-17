package net.gidosa.full.webapp.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.models.dtos.RequestConstructionDto;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import net.gidosa.full.webapp.services.ContactService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/contact")
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/request-construction")
    public String showContactForm(Model model, HttpSession session) {
//        // 세션에서 메시지 가져오기
//        if (session.getAttribute("flashMessage") != null) {
//            model.addAttribute("message", session.getAttribute("flashMessage"));
//            session.removeAttribute("flashMessage");
//        }
//        if (session.getAttribute("flashError") != null) {
//            model.addAttribute("error", session.getAttribute("flashError"));
//            session.removeAttribute("flashError");
//        }
        return "pages/contact/request-construction";
    }

    @PostMapping("/submit")
    public String submitContactForm(RequestConstructionDto request, 
                                  RedirectAttributes redirectAttributes,
                                  HttpSession session) {
        try {
            contactService.submitRequest(request);

            String message = "문의가 성공적으로 접수되었습니다.";
            redirectAttributes.addFlashAttribute("message", message);
//            // 세션에도 메시지 저장
//            session.setAttribute("flashMessage", message);
        } catch (Exception e) {
            log.error("Error submitting contact form", e);

            String error = "문의 접수 중 오류가 발생했습니다. 다시 시도해주세요.";
            redirectAttributes.addFlashAttribute("error", error);
//            // 세션에도 에러 메시지 저장
//            session.setAttribute("flashError", error);
        }
        return "redirect:/contact/request-construction";
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

    @GetMapping("/search")
    @ResponseBody
    public Map<String, Object> searchConstructions(@RequestParam String keyword) {
        List<Construction> results = contactService.searchByKeyword(keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("count", results.size());
        response.put("results", results);
        return response;
    }
}

// Building 클래스
@Data
@AllArgsConstructor
class Building {
    private String name;
    private String imageUrl;
} 