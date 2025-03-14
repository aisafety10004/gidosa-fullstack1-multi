package net.gidosa.full.webadmin.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.RequestService;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/request")
public class RequestController {
    
    private final RequestService requestService;
    
    @GetMapping("/construction/list")
    public String listConstructionRequests(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String searchType,
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(required = false, defaultValue = "id") String sort,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            @RequestParam(required = false) Integer size,
            Model model) {
        
        // 페이지 크기가 지정된 경우 해당 크기로 Pageable 객체 생성
        if (size != null && (size == 10 || size == 20 || size == 30)) {
            pageable = PageRequest.of(pageable.getPageNumber(), size, pageable.getSort());
        }
        
        // Create a new pageable with the sort parameter
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(sortDirection, sort);
        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);
        
        Page<RequestConstruction> requests = requestService.searchRequestConstructions(searchType, searchKeyword, pageableWithSort);
        
        model.addAttribute("requests", requests);
        model.addAttribute("currentSort", sort);
        model.addAttribute("currentDirection", direction);
        
        return "main/request/construction-list";
    }
}
