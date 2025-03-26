package net.gidosa.full.webadmin.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.models.dtos.CustomMenuDto;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.full.webadmin.services.CustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/settings/menu-manager")
public class CustomMenuManagerController {
    
    private final CustomMenuService customMenuService;
    
    /**
     * 메뉴 관리 페이지를 표시합니다.
     */
    @GetMapping
    public String customMenuManager(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        boolean isAdmin = "ROLE_ADMIN".equals(memberAdmin.getRole());
        Construction construction = memberAdmin.getConstruction();
        
        // ROLE_ADMIN이 아니고 construction이 null인 경우에만 리다이렉트
        if (!isAdmin && construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        List<CustomMenu> rootMenus;
        if (isAdmin) {
            // 관리자는 모든 메뉴를 볼 수 있음
//            rootMenus = customMenuService.getAllRootMenusWithChildren();
            rootMenus = customMenuService.getAllRootMenusAdminWithChildren();
        } else {
            // 일반 매니저는 자신의 건설현장 메뉴만 볼 수 있음
            rootMenus = customMenuService.getRootMenusWithChildrenByConstructionId(construction.getId());
        }
        
        model.addAttribute("rootMenus", rootMenus);
        model.addAttribute("isAdmin", isAdmin);
        
        if (!isAdmin) {
            model.addAttribute("constructionId", construction.getId());
        }
        
        return "main/settings/menu-manager/list";
    }
    
    /**
     * 새 메뉴 생성 폼을 표시합니다.
     */
    @GetMapping("/create")
    public String createCustomMenuForm(Model model,
                                @RequestParam(required = false) Long parentId,
                                @RequestParam(required = false) Long constructionId,
                                @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        boolean isAdmin = "ROLE_ADMIN".equals(memberAdmin.getRole());
        Construction construction = memberAdmin.getConstruction();
        
        // ROLE_ADMIN이 아니고 construction이 null인 경우에만 리다이렉트
        if (!isAdmin && construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        CustomMenuDto menuDto = new CustomMenuDto();
        
        // 관리자가 constructionId를 지정한 경우 해당 값 사용, 아니면 현재 로그인한 사용자의 construction
        if (isAdmin && constructionId != null) {
            menuDto.setConstructionId(constructionId);
        } else if (!isAdmin) {
            menuDto.setConstructionId(construction.getId());
        }
        
        menuDto.setParentId(parentId);
        menuDto.setIsActive(true);
        menuDto.setDisplayOrder(0);
        
        model.addAttribute("menuDto", menuDto);
        model.addAttribute("isAdmin", isAdmin);
        
        // 관리자는 모든 건설현장의 루트 메뉴를 볼 수 있음
        if (isAdmin) {
            if (constructionId != null) {
                model.addAttribute("parentMenus", customMenuService.getRootMenusByConstructionId(constructionId));
            } else {
                model.addAttribute("parentMenus", customMenuService.getAllRootMenus());
            }
            // 관리자에게는 건설현장 선택 옵션 제공
            model.addAttribute("constructions", customMenuService.getAllConstructions());
        } else {
            model.addAttribute("parentMenus", customMenuService.getRootMenusByConstructionId(construction.getId()));
        }
        model.addAttribute("isEdit", false);
        
        return "main/settings/menu-manager/form";
    }
    
    /**
     * 메뉴 수정 폼을 표시합니다.
     */
    @GetMapping("/edit/{id}")
    public String editCustomMenuForm(@PathVariable Long id, Model model,
                              @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        boolean isAdmin = "ROLE_ADMIN".equals(memberAdmin.getRole());
        Construction construction = memberAdmin.getConstruction();
        
        if (!isAdmin && construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        CustomMenu menu = customMenuService.getMenuById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + id));
        Construction menuConstruction = menu.getConstruction();

        // 관리자가 아니고, 다른 건설 현장의 메뉴는 수정할 수 없음
        if (!isAdmin && menuConstruction != null && !menuConstruction.getId().equals(construction.getId())) {
            return "redirect:/settings/menu-manager?error=unauthorized";
        }
        
        CustomMenuDto menuDto = customMenuService.convertToDto(menu);
        
        model.addAttribute("menuDto", menuDto);
        model.addAttribute("isAdmin", isAdmin);
        
        // 관리자는 모든 건설현장의 루트 메뉴를 볼 수 있음
        if (isAdmin) {
            model.addAttribute("parentMenus", customMenuService.getAllRootMenus()
                    .stream()
                    .filter(m -> !m.getId().equals(id)) // 자기 자신은 부모가 될 수 없음
                    .collect(Collectors.toList()));
            // 관리자에게는 건설현장 선택 옵션 제공
            model.addAttribute("constructions", customMenuService.getAllConstructions());
        } else {
            model.addAttribute("parentMenus", customMenuService.getRootMenusByConstructionId(construction.getId())
                    .stream()
                    .filter(m -> !m.getId().equals(id)) // 자기 자신은 부모가 될 수 없음
                    .collect(Collectors.toList()));
        }
        model.addAttribute("isEdit", true);
        
        return "main/settings/menu-manager/form";
    }
    
    /**
     * 메뉴를 저장합니다.
     */
    @PostMapping("/save")
    public String saveCustomMenu(@Valid @ModelAttribute("menuDto") CustomMenuDto menuDto,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes,
                          @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        boolean isAdmin = "ROLE_ADMIN".equals(memberAdmin.getRole());
        Construction construction = memberAdmin.getConstruction();
        
        if (!isAdmin && construction == null) {
            return "redirect:/main?error=no-construction";
        }

        // 관리자가 아니고, 다른 건설 현장의 메뉴는 저장할 수 없음
        if (!isAdmin && !construction.getId().equals(menuDto.getConstructionId())) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/settings/menu-manager";
        }
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("isAdmin", isAdmin);
            
            if (isAdmin) {
                model.addAttribute("parentMenus", customMenuService.getAllRootMenus());
                model.addAttribute("constructions", customMenuService.getAllConstructions());
            } else {
                model.addAttribute("parentMenus", customMenuService.getRootMenusByConstructionId(construction.getId()));
            }
            
            model.addAttribute("isEdit", menuDto.getId() != null);
            return "main/settings/menu-manager/form";
        }
        
        try {
            CustomMenu savedMenu = customMenuService.saveMenu(menuDto);
            redirectAttributes.addFlashAttribute("message", "메뉴가 저장되었습니다.");
            return "redirect:/settings/menu-manager";
        } catch (Exception e) {
            log.error("메뉴 저장 중 오류 발생", e);
            model.addAttribute("error", "메뉴 저장 중 오류가 발생했습니다: " + e.getMessage());
            model.addAttribute("isAdmin", isAdmin);
            
            if (isAdmin) {
                model.addAttribute("parentMenus", customMenuService.getAllRootMenus());
                model.addAttribute("constructions", customMenuService.getAllConstructions());
            } else {
                model.addAttribute("parentMenus", customMenuService.getRootMenusByConstructionId(construction.getId()));
            }
            
            model.addAttribute("isEdit", menuDto.getId() != null);
            return "main/settings/menu-manager/form";
        }
    }
    
    /**
     * 메뉴를 삭제합니다.
     */
    @PostMapping("/delete/{id}")
    public String deleteCustomMenu(@PathVariable Long id,
                            RedirectAttributes redirectAttributes,
                            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        MemberAdmin memberAdmin = principalDetails.getMemberAdmin();
        boolean isAdmin = "ROLE_ADMIN".equals(memberAdmin.getRole());
        Construction construction = memberAdmin.getConstruction();
        
        if (!isAdmin && construction == null) {
            return "redirect:/main?error=no-construction";
        }
        
        try {
            CustomMenu menu = customMenuService.getMenuById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + id));

            Construction menuConstruction = menu.getConstruction();
            // 관리자가 아니고, 다른 건설 현장의 메뉴는 삭제할 수 없음
            if (!isAdmin && menuConstruction != null && !menuConstruction.getId().equals(construction.getId())) {
                redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
                return "redirect:/settings/menu-manager";
            }
            
            customMenuService.deleteMenu(id);
            redirectAttributes.addFlashAttribute("message", "메뉴가 삭제되었습니다.");
        } catch (Exception e) {
            log.error("메뉴 삭제 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "메뉴 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "redirect:/settings/menu-manager";
    }
} 