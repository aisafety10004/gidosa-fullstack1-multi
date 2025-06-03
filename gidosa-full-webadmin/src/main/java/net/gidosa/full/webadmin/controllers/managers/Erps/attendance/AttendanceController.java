package net.gidosa.full.webadmin.controllers.managers.Erps.attendance;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/erp")
public class AttendanceController {

    // private final ErpService erpService;

    @GetMapping("/attendance")
    public String attendance() {
        log.info("근퇴 관리 페이지 접속");
        return "main/erp/attendance/main";
    }

}
