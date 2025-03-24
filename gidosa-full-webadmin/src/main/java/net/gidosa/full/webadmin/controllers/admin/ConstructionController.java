package net.gidosa.full.webadmin.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.ConstructionService;
import net.gidosa.full.webadmin.services.CustomMenuService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/construction")
public class ConstructionController {
    private final ConstructionService constructionService;
    private final CustomMenuService customMenuService;

    @GetMapping("/list")
    public String list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @PageableDefault(size = 10) Pageable pageable, 
            Model model) {
        
        // 검색 파라미터 로깅 및 트림 처리
        log.debug("검색 요청 - 현장명: {}, 위치: {}, 상태: {}, 정렬: {}, 방향: {}, 페이지 크기: {}", 
                 name, location, status, sort, direction, size);
        
        // 정렬 방향 설정
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? 
                                      Sort.Direction.DESC : Sort.Direction.ASC;
        
        // 검색 실행
        Page<Construction> constructions = constructionService.searchConstructions(
                name, location, status, sort, sortDirection, size, pageable.getPageNumber());
        
        log.debug("검색 결과 - 총 {}개 항목 찾음", constructions.getTotalElements());
        
        model.addAttribute("constructions", constructions);
        model.addAttribute("name", name);
        model.addAttribute("location", location);
        model.addAttribute("status", status);
        model.addAttribute("sort", sort);
        model.addAttribute("direction", direction);
        model.addAttribute("size", size);
        
        return "main/construction/list";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        Construction construction = new Construction();
        model.addAttribute("construction", construction);
        
        // 템플릿 커스텀 메뉴 로딩 (construction_id가 null인 메뉴)
        List<CustomMenu> templateCustomMenus = customMenuService.getTemplateCustomMenus();
        model.addAttribute("templateCustomMenus", templateCustomMenus);
        
        return "main/construction/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute Construction construction) {
        // 선택된 커스텀 메뉴 ID 임시 저장
        List<Long> selectedMenuIds = construction.getSelectedCustomMenuIds();
        
        // 건설 현장 저장
        Construction savedConstruction = constructionService.saveConstruction(construction);
        
        // 선택된 커스텀 메뉴가 있으면 복사
        if (selectedMenuIds != null && !selectedMenuIds.isEmpty()) {
            constructionService.copyCustomMenusToConstruction(savedConstruction.getId(), selectedMenuIds);
        }
        
        return "redirect:/construction/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Construction construction = constructionService.getConstructionWithManagementMenus(id);
        model.addAttribute("construction", construction);
        
        // 템플릿 커스텀 메뉴 로딩 (construction_id가 null인 메뉴)
        List<CustomMenu> templateCustomMenus = customMenuService.getTemplateCustomMenus();
        model.addAttribute("templateCustomMenus", templateCustomMenus);
        
        // 현재 선택된 커스텀 메뉴 ID 로딩
        List<Long> selectedCustomMenuIds = constructionService.getSelectedCustomMenuIds(id);
        construction.setSelectedCustomMenuIds(selectedCustomMenuIds);
        
        return "main/construction/edit";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @ModelAttribute Construction construction) {
        // 선택된 커스텀 메뉴 ID 임시 저장
        List<Long> selectedMenuIds = construction.getSelectedCustomMenuIds();
        
        construction.setId(id);
        constructionService.saveConstruction(construction);

        // 선택된 커스텀 메뉴 업데이트
        if (selectedMenuIds != null) {
            constructionService.updateCustomMenusForConstruction(id, selectedMenuIds);
        }

        return "redirect:/construction/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        // 건설 현장 삭제시 관련 커스텀 메뉴도 함께 삭제
        constructionService.deleteConstructionWithCustomMenus(id);
        return "redirect:/construction/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Construction construction = constructionService.getConstructionWithManagementMenus(id);
        
        // 현재 건설 현장의 커스텀 메뉴 로딩
        List<CustomMenu> customMenus = customMenuService.getAllMenusByConstructionId(id);
        construction.setCustomMenus(customMenus);
        
        model.addAttribute("construction", construction);
        return "main/construction/detail";
    }
}
