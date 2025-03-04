package net.gidosa.full.webadmin.controllers.managers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.models.dtos.RiskFactorDto;
import net.gidosa.full.webadmin.models.dtos.RiskFactorSearchDto;
import net.gidosa.full.webadmin.services.RiskFactorService;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/safety/risk-factor")
public class RiskFactorController {
    
    private final RiskFactorService riskFactorService;
    
    @GetMapping({"", "/"})
    public String index() {
        return "redirect:/safety/risk-factor/list";
    }
    
    @GetMapping("/list")
    public String list(
            @ModelAttribute RiskFactorSearchDto searchDto,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            Model model,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        
        Long constructionId = principalDetails.getMemberAdmin().getConstruction().getId();
        
        Page<RiskFactor> riskFactorsPage = riskFactorService.searchRiskFactors(
                constructionId,
                searchDto.getName(),
                searchDto.getStartDate(),
                searchDto.getEndDate(),
                pageable
        );
        
        model.addAttribute("riskFactors", riskFactorsPage);
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("statusOptions", RiskFactor.RiskStatus.values());
        model.addAttribute("riskLevelOptions", RiskFactor.RiskLevel.values());
        
        return "main/safety/risk-factor/list";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("riskFactorDto", new RiskFactorDto());
        model.addAttribute("statusOptions", RiskFactor.RiskStatus.values());
        model.addAttribute("riskLevelOptions", RiskFactor.RiskLevel.values());
        return "main/safety/risk-factor/register";
    }
    
    @PostMapping("/register")
    public String register(
            @ModelAttribute RiskFactorDto riskFactorDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            Long constructionId = principalDetails.getMemberAdmin().getConstruction().getId();
            RiskFactor riskFactor = riskFactorDto.toEntity();
            riskFactorService.createRiskFactor(constructionId, riskFactor);
            redirectAttributes.addFlashAttribute("message", "위험요인이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            log.error("Error registering risk factor", e);
            redirectAttributes.addFlashAttribute("error", "위험요인 등록 중 오류가 발생했습니다.");
        }
        
        return "redirect:/safety/risk-factor/list";
    }
    
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        riskFactorService.getRiskFactorById(id)
                .ifPresent(riskFactor -> {
                    model.addAttribute("riskFactor", riskFactor);
                });
        return "main/safety/risk-factor/detail";
    }
    
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        riskFactorService.getRiskFactorById(id)
                .ifPresent(riskFactor -> {
                    model.addAttribute("riskFactorDto", RiskFactorDto.fromEntity(riskFactor));
                    model.addAttribute("statusOptions", RiskFactor.RiskStatus.values());
                    model.addAttribute("riskLevelOptions", RiskFactor.RiskLevel.values());
                });
        return "main/safety/risk-factor/update";
    }
    
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute RiskFactorDto riskFactorDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            RiskFactor riskFactor = riskFactorDto.toEntity();
            riskFactorService.updateRiskFactor(id, riskFactor);
            redirectAttributes.addFlashAttribute("message", "위험요인이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            log.error("Error updating risk factor", e);
            redirectAttributes.addFlashAttribute("error", "위험요인 수정 중 오류가 발생했습니다.");
        }
        
        return "redirect:/safety/risk-factor/detail/" + id;
    }
    
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            riskFactorService.deleteRiskFactor(id);
            redirectAttributes.addFlashAttribute("message", "위험요인이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            log.error("Error deleting risk factor", e);
            redirectAttributes.addFlashAttribute("error", "위험요인 삭제 중 오류가 발생했습니다.");
        }
        
        return "redirect:/safety/risk-factor/list";
    }
}
