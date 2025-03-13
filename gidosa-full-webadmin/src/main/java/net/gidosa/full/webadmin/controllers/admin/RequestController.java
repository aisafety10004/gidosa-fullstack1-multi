package net.gidosa.full.webadmin.controllers.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.services.RequestService;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
            Model model) {
        Page<RequestConstruction> requests = requestService.searchRequestConstructions(searchType, searchKeyword, pageable);
        model.addAttribute("requests", requests);
        return "main/request/construction-list";
    }
}
