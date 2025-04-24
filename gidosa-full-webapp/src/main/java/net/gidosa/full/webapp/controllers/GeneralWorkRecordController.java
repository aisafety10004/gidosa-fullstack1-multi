package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.full.webapp.services.GeneralWorkRecordService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.models.entities.dbs.mysql.WorkRecordLeave;
import net.gidosa.rdb.models.entities.dbs.mysql.WorkRecordStart;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/work-record")
public class GeneralWorkRecordController {
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;
    private final GeneralWorkRecordService generalWorkRecordService;

    @GetMapping("/start")
    public String start(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
//        Long constructionId = 1L;

        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);

        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/work-record/start";
    }

    @PostMapping("/start")
    public String processStart(@AuthenticationPrincipal PrincipalDetails principalDetails,
                              @RequestParam("name") String name,
                              @RequestParam("phone") String phone,
                              @RequestParam(value = "healthCheck", required = false) Boolean healthCheck,
                              @RequestParam("workerStartPhoto") MultipartFile workerStartPhoto,
                              @RequestParam(value = "planToGoOut", required = false) Boolean planToGoOut,
                              @RequestParam(value = "planToLeaveEarly", required = false) Boolean planToLeaveEarly,
                              @RequestParam(value = "planToWorkOvertime", required = false) Boolean planToWorkOvertime,
                              @RequestParam(value = "safetyRuleCheck", required = false) Boolean safetyRuleCheck,
                              @RequestParam(value = "riskReportCheck", required = false) Boolean riskReportCheck,
                              @RequestParam(value = "protectiveGearCheck", required = false) Boolean protectiveGearCheck,
                              RedirectAttributes redirectAttributes) {
        
        try {
            // null 값 처리 - 체크되지 않은 체크박스는 null로 들어오므로 false로 설정
            healthCheck = healthCheck != null && healthCheck;
            planToGoOut = planToGoOut != null && planToGoOut;
            planToLeaveEarly = planToLeaveEarly != null && planToLeaveEarly;
            planToWorkOvertime = planToWorkOvertime != null && planToWorkOvertime;
            safetyRuleCheck = safetyRuleCheck != null && safetyRuleCheck;
            riskReportCheck = riskReportCheck != null && riskReportCheck;
            protectiveGearCheck = protectiveGearCheck != null && protectiveGearCheck;
            
            // 출근 기록 저장
            WorkRecordStart workRecordStart = generalWorkRecordService.saveWorkRecordStart(
                principalDetails.getMemberGeneral(),
                principalDetails.getMemberGeneral().getConstruction(),
                name,
                phone,
                healthCheck,
                workerStartPhoto,
                planToGoOut,
                planToLeaveEarly,
                planToWorkOvertime,
                safetyRuleCheck,
                riskReportCheck,
                protectiveGearCheck
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "출근등록이 완료되었습니다.");
            // return "redirect:/general/main/main";            
        } catch (Exception e) {
            log.error("출근 처리 중 오류 발생: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/general/work-record/start";
    }

    @GetMapping("/leave")
    public String leave(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
//        Long constructionId = 1L;

        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);

        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);

        return "pages/general/work-record/leave";
    }
    
    @PostMapping("/leave")
    public String processLeave(@AuthenticationPrincipal PrincipalDetails principalDetails,
                               @RequestParam("name") String name,
                               @RequestParam("phone") String phone,
                               @RequestParam(value = "bestEffortCheck", required = false) Boolean bestEffortCheck,
                               @RequestParam(value = "noAccidentCheck", required = false) Boolean noAccidentCheck,
                               @RequestParam("workerLeavePhoto") MultipartFile workerLeavePhoto,
                               @RequestParam(value = "improvementSuggestions", required = false) String improvementSuggestions,
                               @RequestParam(value = "planToComeNextDay", required = true) Boolean planToComeNextDay,
                               RedirectAttributes redirectAttributes) {
        
        try {
            // null 값 처리 - 체크되지 않은 체크박스는 null로 들어오므로 false로 설정
            bestEffortCheck = bestEffortCheck != null && bestEffortCheck;
            noAccidentCheck = noAccidentCheck != null && noAccidentCheck;
            
            // 체크박스 유효성 검사 - 최소 1개 이상 체크되어야 함
            if (!bestEffortCheck && !noAccidentCheck) {
                redirectAttributes.addFlashAttribute("errorMessage", "업무수행결과 점검 항목은 최소 1개 이상 선택해야 합니다.");
                return "redirect:/general/work-record/leave";
            }
            
            // 사진 유효성 검사
            if (workerLeavePhoto == null || workerLeavePhoto.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "퇴근 근로자 사진은 필수 등록 항목입니다.");
                return "redirect:/general/work-record/leave";
            }
            
            // 퇴근 기록 저장
            WorkRecordLeave workRecordLeave = generalWorkRecordService.saveWorkRecordLeave(
                principalDetails.getMemberGeneral(),
                principalDetails.getMemberGeneral().getConstruction(),
                name,
                phone,
                bestEffortCheck,
                noAccidentCheck,
                workerLeavePhoto,
                improvementSuggestions,
                planToComeNextDay
            );
            
            redirectAttributes.addFlashAttribute("successMessage", "퇴근등록이 완료되었습니다.");
            // return "redirect:/general/main/main";
        } catch (Exception e) {
            log.error("퇴근 처리 중 오류 발생: " + e.getMessage(), e);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/general/work-record/leave";
    }
}
