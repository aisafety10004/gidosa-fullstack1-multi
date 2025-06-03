package net.gidosa.full.webadmin.controllers.managers.Erps.approval;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import groovy.util.logging.Log4j2;
import lombok.RequiredArgsConstructor;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/erp")
public class ApprovalController {

    @GetMapping("/approval")
    public String approval() {
        return "main/erp/approval/main";
    }

}
