package net.gidosa.full.webadmin.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType1;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType2;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType3;
import net.gidosa.full.webadmin.services.CustomMenuContentService;
import net.gidosa.full.webadmin.services.CustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/custom")
public class CustomMenuContentController {
    
    private final CustomMenuService customMenuService;
    private final CustomMenuContentService customMenuContentService;
    
    /**
     * 커스텀 메뉴 컨텐츠 페이지를 표시합니다.
     */
    @GetMapping("/**")
    public String handleCustomMenuRequest(HttpServletRequest request, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
        
        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        // 현재 요청 URL 가져오기 - HttpServletRequest를 사용하여 직접 경로 추출
        String requestURI = request.getRequestURI();
        log.info("Requested URI: {}", requestURI);
        
        // 메뉴 정보 조회 - 전체 경로로 조회
        CustomMenu menu = customMenuService.getMenuByUrl(construction.getId(), requestURI);
        
        if (menu == null) {
            log.error("Menu not found for URL: {}", requestURI);
            return "redirect:/main?error=menu-not-found";
        }
        
        model.addAttribute("menu", menu);
        
        // 메뉴 타입에 따라 다른 처리
        if (menu.getMenuType() == 1) { // 단건 내용 저장/보기
            CustomMenuContentType1 content = customMenuContentService.getType1ContentByMenuId(menu.getId());
            model.addAttribute("content", content != null ? content : new CustomMenuContentType1());
            return "main/custom/type1";
        } else if (menu.getMenuType() == 2) { // 날짜 저장/보기
            LocalDate selectedDate = LocalDate.now();
            CustomMenuContentType2 content = customMenuContentService.getType2ContentByMenuIdAndDate(menu.getId(), selectedDate);
            model.addAttribute("content", content != null ? content : new CustomMenuContentType2());
            model.addAttribute("selectedDate", selectedDate);
            return "main/custom/type2";
        } else if (menu.getMenuType() == 3) { // Mermaid 저장/보기
            CustomMenuContentType3 content = customMenuContentService.getType3ContentByMenuId(menu.getId());
            model.addAttribute("content", content != null ? content : new CustomMenuContentType3());
            return "main/custom/type3";
        } else {
            return "redirect:/main?error=invalid-menu-type";
        }
    }
    
    /**
     * 타입1 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type1/{menuId}")
    public String saveType1Content(@PathVariable Long menuId,
                                  @RequestParam String content,
                                  RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
        
        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!menu.getConstruction().getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            customMenuContentService.saveType1Content(menuId, content);
            redirectAttributes.addFlashAttribute("message", "내용이 저장되었습니다.");
            
            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입1 컨텐츠 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }
    
    /**
     * 타입2 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type2/{menuId}")
    public String saveType2Content(@PathVariable Long menuId,
                                  @RequestParam String content,
                                  @RequestParam String selectedDate,
                                  RedirectAttributes redirectAttributes,
                                  @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
        
        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!menu.getConstruction().getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            LocalDate date = LocalDate.parse(selectedDate);
            customMenuContentService.saveType2Content(menuId, content, date);
            redirectAttributes.addFlashAttribute("message", "내용이 저장되었습니다.");
            
            return "redirect:" + menu.getUrl() + "?date=" + selectedDate;
        } catch (Exception e) {
            log.error("타입2 컨텐츠 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }
    
    /**
     * 타입2 메뉴에서 날짜를 선택했을 때 해당 날짜의 컨텐츠를 조회합니다.
     */
    @GetMapping("/type2/{menuId}")
    public String getType2ContentByDate(@PathVariable Long menuId,
                                       @RequestParam(required = false) String date,
                                       Model model,
                                       RedirectAttributes redirectAttributes,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
        
        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!menu.getConstruction().getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();
            CustomMenuContentType2 content = customMenuContentService.getType2ContentByMenuIdAndDate(menuId, selectedDate);
            
            model.addAttribute("menu", menu);
            model.addAttribute("content", content != null ? content : new CustomMenuContentType2());
            model.addAttribute("selectedDate", selectedDate);
            
            return "main/custom/type2";
        } catch (Exception e) {
            log.error("타입2 컨텐츠 조회 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 조회 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입1 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type1/{menuId}")
    public String deleteType1Content(@PathVariable Long menuId,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
        
        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!menu.getConstruction().getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            customMenuContentService.deleteType1Content(menuId);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");
            
            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입1 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입2 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type2/{menuId}")
    public String deleteType2Content(@PathVariable Long menuId,
                                   @RequestParam String date,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();
        
        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
            
            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!menu.getConstruction().getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }
            
            LocalDate selectedDate = LocalDate.parse(date);
            customMenuContentService.deleteType2Content(menuId, selectedDate);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");
            
            return "redirect:" + menu.getUrl() + "?date=" + date;
        } catch (Exception e) {
            log.error("타입2 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }
    
    /**
     * 타입3 메뉴 컨텐츠를 저장합니다.
     */
    @PostMapping("/save/type3/{menuId}")
    public String saveType3Content(@PathVariable Long menuId,
//                                 @RequestParam String content,
                                 @RequestParam(value = "mermaidCode", required = false) String content,
                                 RedirectAttributes redirectAttributes,
                                 @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();

        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }

        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));

            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!menu.getConstruction().getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }

            customMenuContentService.saveType3Content(menuId, content);
            redirectAttributes.addFlashAttribute("message", "내용이 저장되었습니다.");

            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입3 컨텐츠 저장 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 저장 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }

    /**
     * 타입3 메뉴 컨텐츠를 삭제합니다.
     */
    @GetMapping("/delete/type3/{menuId}")
    public String deleteType3Content(@PathVariable Long menuId,
                                   RedirectAttributes redirectAttributes,
                                   @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        Construction construction = memberAdmin.getConstruction();

        if (construction == null) {
            return "redirect:/main?error=no-construction";
        }

        try {
            CustomMenu menu = customMenuService.getMenuById(menuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));

            // 다른 건설 현장의 메뉴는 접근할 수 없음
            if (!menu.getConstruction().getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/main";
            }

            customMenuContentService.deleteType3Content(menuId);
            redirectAttributes.addFlashAttribute("message", "내용이 삭제되었습니다.");

            return "redirect:" + menu.getUrl();
        } catch (Exception e) {
            log.error("타입3 컨텐츠 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "내용 삭제 중 오류가 발생했습니다: " + e.getMessage());
            return "redirect:/main";
        }
    }
} 