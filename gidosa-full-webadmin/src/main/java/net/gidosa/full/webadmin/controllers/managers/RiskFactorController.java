package net.gidosa.full.webadmin.controllers.managers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.models.dtos.RiskFactorDto;
import net.gidosa.full.webadmin.models.dtos.RiskFactorSearchDto;
import net.gidosa.full.webadmin.models.dtos.RiskFactorUpdateDto;
import net.gidosa.full.webadmin.services.FileStorageService;
import net.gidosa.full.webadmin.services.RiskFactorService;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/safety/risk-factor")
public class RiskFactorController {
    
    private final RiskFactorService riskFactorService;
    private final FileStorageService fileStorageService;
    
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
        
        Page<RiskFactor> riskFactorsPage = riskFactorService.searchRiskFactorsByExecutionDate(
                constructionId,
                searchDto.getSiteName(),
                searchDto.getExecutionDateStart(),
                searchDto.getExecutionDateEnd(),
                pageable
        );
        
        model.addAttribute("riskFactors", riskFactorsPage);
        model.addAttribute("searchDto", searchDto);

        return "main/safety/risk-factor/list";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        RiskFactorDto riskFactorDto = new RiskFactorDto();
        // Set default values
        riskFactorDto.setRiskPossibility((byte) 1);
        riskFactorDto.setRiskCriticality((byte) 1);
        riskFactorDto.setRiskSize((short) 1);
        
        model.addAttribute("riskFactorDto", riskFactorDto);
        addCommonModelAttributes(model);
        return "main/safety/risk-factor/register";
    }
    
    @PostMapping("/register")
    public String register(
            @ModelAttribute RiskFactorDto riskFactorDto,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            Long constructionId = principalDetails.getMemberAdmin().getConstruction().getId();
            
            // 파일 업로드 처리
            processFileUploads(riskFactorDto);
            
            // 빈 값 처리 (null 또는 0인 경우 기본값 설정)
            if (riskFactorDto.getRiskPossibility() == 0) {
                riskFactorDto.setRiskPossibility((byte) 1);
            }
            
            if (riskFactorDto.getRiskCriticality() == 0) {
                riskFactorDto.setRiskCriticality((byte) 1);
            }
            
            // 위험성 크기 계산 (가능성 * 중대성)
            riskFactorDto.setRiskSize((short) (riskFactorDto.getRiskPossibility() * riskFactorDto.getRiskCriticality()));
            
            RiskFactor riskFactor = riskFactorDto.toEntity();
            riskFactorService.createRiskFactor(constructionId, riskFactor);
            redirectAttributes.addFlashAttribute("message", "위험요인이 성공적으로 신규등록되었습니다.");
        } catch (Exception e) {
            log.error("Error registering risk factor", e);
            redirectAttributes.addFlashAttribute("error", "위험요인 신규등록 중 오류가 발생했습니다: " + e.getMessage());
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
        Optional<RiskFactor> riskFactorOpt = riskFactorService.getRiskFactorById(id);
        if (riskFactorOpt.isPresent()) {
            RiskFactor riskFactor = riskFactorOpt.get();
            RiskFactorUpdateDto riskFactorUpdateDto = RiskFactorUpdateDto.fromEntity(riskFactor);
            
            // Ensure values are not null or zero
            if (riskFactorUpdateDto.getRiskPossibility() == 0) {
                riskFactorUpdateDto.setRiskPossibility((byte) 1);
            }
            if (riskFactorUpdateDto.getRiskCriticality() == 0) {
                riskFactorUpdateDto.setRiskCriticality((byte) 1);
            }
            if (riskFactorUpdateDto.getRiskSize() == 0) {
                riskFactorUpdateDto.setRiskSize((short) 1);
            }
            
            model.addAttribute("riskFactorDto", riskFactorUpdateDto);
            addCommonModelAttributes(model);
            return "main/safety/risk-factor/update";
        }
        return "redirect:/safety/risk-factor/list";
    }
    
    @PostMapping("/update/{id}")
    public String update(
            @PathVariable Long id,
            @ModelAttribute("riskFactorDto") RiskFactorUpdateDto riskFactorUpdateDto,
            RedirectAttributes redirectAttributes) {
        
        try {
            // 파일 업로드 처리
            processFileUploads(riskFactorUpdateDto);
            
            // 빈 값 처리 (null 또는 0인 경우 기본값 설정)
            if (riskFactorUpdateDto.getRiskPossibility() == 0) {
                riskFactorUpdateDto.setRiskPossibility((byte) 1);
            }
            
            if (riskFactorUpdateDto.getRiskCriticality() == 0) {
                riskFactorUpdateDto.setRiskCriticality((byte) 1);
            }
            
            // 위험성 크기 계산 (가능성 * 중대성)
            riskFactorUpdateDto.setRiskSize((short) (riskFactorUpdateDto.getRiskPossibility() * riskFactorUpdateDto.getRiskCriticality()));
            
            riskFactorService.updateRiskFactorFromUpdateDto(id, riskFactorUpdateDto);
            redirectAttributes.addFlashAttribute("message", "위험요인이 성공적으로 개선되었습니다.");
        } catch (Exception e) {
            log.error("Error updating risk factor", e);
            redirectAttributes.addFlashAttribute("error", "위험요인 개선 중 오류가 발생했습니다: " + e.getMessage());
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
    
    // 공통 모델 속성 추가 메서드
    private void addCommonModelAttributes(Model model) {
        model.addAttribute("riskClassificationOptions", RiskFactor.RiskClassification.values());
        model.addAttribute("riskDetailFactorOptions", RiskFactor.RiskDetailFactor.values());
        model.addAttribute("riskReductionMeasureFirstOptions", RiskFactor.RiskReductionMeasureFirst.values());
        model.addAttribute("riskMeasureCompletionOptions", RiskFactor.RiskMeasureCompletion.values());
        
        // 추가 필드 옵션
        model.addAttribute("riskFactorEvaluationTypeOptions", RiskFactor.RiskFactorEvaluationType.values());
        model.addAttribute("authorityDivisionLevelOptions", RiskFactor.RiskFactorAuthorityDivisionLevel.values());
        
        // 관계 엔티티 옵션 - 실제 구현에서는 서비스에서 가져와야 함
        // 임시 구현 (실제 프로젝트에서는 서비스를 통해 DB에서 가져와야 함)
        model.addAttribute("authorityDivisionOptions", getMockAuthorityDivisions());
        model.addAttribute("divisionDetailOptions", getMockDivisionDetails());
    }
    
    // 임시 구현: 실제 프로젝트에서는 서비스를 통해 DB에서 가져와야 함
    private List<MockEntityOption> getMockAuthorityDivisions() {
        List<MockEntityOption> options = new ArrayList<>();
        options.add(new MockEntityOption(1L, "서울특별시교육청"));
        options.add(new MockEntityOption(2L, "경기도교육청"));
        options.add(new MockEntityOption(3L, "인천광역시교육청"));
        return options;
    }
    
    // 임시 구현: 실제 프로젝트에서는 서비스를 통해 DB에서 가져와야 함
    private List<MockEntityOption> getMockDivisionDetails() {
        List<MockEntityOption> options = new ArrayList<>();
        options.add(new MockEntityOption(1L, "서울특별시교육청 본청"));
        options.add(new MockEntityOption(2L, "서울특별시동부교육지원청"));
        options.add(new MockEntityOption(3L, "서울특별시서부교육지원청"));
        options.add(new MockEntityOption(4L, "경기도교육청 본청"));
        options.add(new MockEntityOption(5L, "경기도수원교육지원청"));
        return options;
    }
    
    // 임시 엔티티 옵션 클래스
    @Data
    @AllArgsConstructor
    private static class MockEntityOption {
        private Long id;
        private String name;
    }
    
    // 파일 업로드 처리 메서드
    private void processFileUploads(RiskFactorDto riskFactorDto) throws IOException {
        MultipartFile workImage1File = riskFactorDto.getWorkImage1File();
        if (workImage1File != null && !workImage1File.isEmpty()) {
            //String workImage1Url = fileStorageService.storeFile(workImage1File, "risk-factors");
            String workImage1Url = fileStorageService.storeFile(workImage1File, Strings.EMPTY);
            riskFactorDto.setWorkImage1Url(workImage1Url);
        }
        
        MultipartFile workImage2File = riskFactorDto.getWorkImage2File();
        if (workImage2File != null && !workImage2File.isEmpty()) {
            //String workImage2Url = fileStorageService.storeFile(workImage2File, "risk-factors");
            String workImage2Url = fileStorageService.storeFile(workImage2File, Strings.EMPTY);
            riskFactorDto.setWorkImage2Url(workImage2Url);
        }
    }
}
