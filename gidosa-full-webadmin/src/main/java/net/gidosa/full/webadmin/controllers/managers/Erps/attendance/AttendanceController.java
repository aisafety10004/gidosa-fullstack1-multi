package net.gidosa.full.webadmin.controllers.managers.Erps.attendance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.models.dtos.AttendanceDto;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/erp")
public class AttendanceController {

    // private final ErpService erpService;

    @GetMapping("/attendance")
    public String attendance(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        List<AttendanceDto> attendanceList = new ArrayList<>();

        attendanceList.add(AttendanceDto.builder()
                .name("홍길동")
                .workStartTime(LocalDateTime.now().minusDays(1))
                .workEndTime(LocalDateTime.now())
                .attendanceType("workEnd")
                .date(LocalDate.now())
                .build());

        attendanceList.add(AttendanceDto.builder()
                .name("김미영")
                .workStartTime(LocalDateTime.now().minusDays(1))
                .attendanceType("workStart")
                .date(LocalDate.now())
                .build());

        model.addAttribute("attendanceList", attendanceList);
        model.addAttribute("attendanceType", "WORKING");
        return "main/erp/attendance/main";
    }

    @PostMapping("/attendance/register")
    @ResponseBody
    public AttendanceDto registerAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        log.info("registerAttendance");

        // 새로운 출퇴근 데이터 생성
        AttendanceDto newAttendance = AttendanceDto.builder()
                .name("관리자") // 실제로는 userDetails에서 사용자 정보를 가져와야 함
                .workStartTime(LocalDateTime.now().minusDays(1))
                .attendanceType("WORKING")
                .date(LocalDate.now())
                .build();

        model.addAttribute("attendance", newAttendance);
        model.addAttribute("attendanceType", "WORKING");
        return newAttendance;
    }

}
