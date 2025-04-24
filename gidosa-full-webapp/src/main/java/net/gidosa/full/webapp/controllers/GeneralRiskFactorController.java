package net.gidosa.full.webapp.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import net.gidosa.full.webapp.dtos.RiskFactorDto;
import net.gidosa.full.webapp.services.GeneralConstructionService;
import net.gidosa.full.webapp.services.GeneralCustomMenuService;
import net.gidosa.full.webapp.services.GeneralRiskFactorService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@RequestMapping("/general/risk-factor")
public class GeneralRiskFactorController {
    private final GeneralConstructionService generalConstructionService;
    private final GeneralCustomMenuService generalCustomMenuService;
    private final GeneralRiskFactorService generalRiskFactorService;

    @GetMapping("/create")
    public String start(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();

        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);

        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);
        
        // DTO 객체 생성 및 모델에 추가
        RiskFactorDto riskFactorDto = new RiskFactorDto();
        riskFactorDto.setConstructionId(constructionId);
        riskFactorDto.setSiteName(construction.getName());
        model.addAttribute("riskFactorDto", riskFactorDto);
        
        // 위험 분류 옵션 추가
        model.addAttribute("riskClassificationOptions", RiskFactor.RiskClassification.values());
        
        // 위험 감소대책 옵션 추가
        model.addAttribute("riskReductionMeasureFirstOptions", RiskFactor.RiskReductionMeasureFirst.values());
        
        // 조치여부 옵션 추가
        model.addAttribute("riskMeasureCompletionOptions", RiskFactor.RiskMeasureCompletion.values());
        
        // 위험 분류별 상세 요인 옵션 추가
        addRiskDetailFactorOptions(model);

        return "pages/general/risk-factor/create";
    }
    
    @PostMapping("/create")
    public String createRiskFactor(
            @ModelAttribute RiskFactorDto riskFactorDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal PrincipalDetails principalDetails,
            RedirectAttributes redirectAttributes) throws IOException {
        
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
        
        // 유효성 검사 결과 오류가 있으면 다시 폼으로
        if (bindingResult.hasErrors()) {
            return "pages/general/risk-factor/create";
        }
        
        // 위험요인 저장
        Long riskFactorId = generalRiskFactorService.saveRiskFactor(riskFactorDto, constructionId);
        
        // 리다이렉트 시 메시지 추가
        redirectAttributes.addFlashAttribute("message", "위험요인이 성공적으로 등록되었습니다.");
        
        // return "redirect:/general/risk-factor/list";
        return "redirect:/general/risk-factor/create";
    }
    
    // 위험 분류별 상세 요인 옵션 생성 메서드
    private void addRiskDetailFactorOptions(Model model) {
        // 기계・장비
        List<Map<String, String>> mechanicalEquipmentOptions = Arrays.asList(
                createOption("축・기어・벨트에 끼임", "축・기어・벨트에 끼임"),
                createOption("구동부에 감김", "구동부에 감김"),
                createOption("회전체에 접촉", "회전체에 접촉"),
                createOption("작업장비에 충돌", "작업장비에 충돌"),
                createOption("낙하・비래", "낙하・비래"),
                createOption("협착", "협착")
        );
        model.addAttribute("mechanicalEquipmentOptions", mechanicalEquipmentOptions);
        
        // 전기 관련
        List<Map<String, String>> electricalOptions = Arrays.asList(
                createOption("충전부 접촉감전", "충전부 접촉감전"),
                createOption("정전기에 의한 감전", "정전기에 의한 감전"),
                createOption("누전에 의한 감전", "누전에 의한 감전"),
                createOption("전선접속부 발열", "전선접속부 발열")
        );
        model.addAttribute("electricalOptions", electricalOptions);
        
        // 화학물질 관련
        List<Map<String, String>> chemicalSubstanceOptions = Arrays.asList(
                createOption("누출에 의한 중독", "누출에 의한 중독"),
                createOption("환기부족에 의한 중독", "환기부족에 의한 중독"),
                createOption("피부접촉에 의한 화상", "피부접촉에 의한 화상"),
                createOption("분진에 의한 호흡기 질환", "분진에 의한 호흡기 질환")
        );
        model.addAttribute("chemicalSubstanceOptions", chemicalSubstanceOptions);
        
        // 생물학적 유해인자
        List<Map<String, String>> biologicalOptions = Arrays.asList(
                createOption("세균에 의한 질병", "세균에 의한 질병"),
                createOption("바이러스에 의한 질병", "바이러스에 의한 질병"),
                createOption("곰팡이에 의한 질병", "곰팡이에 의한 질병"),
                createOption("기생충에 의한 질병", "기생충에 의한 질병")
        );
        model.addAttribute("biologicalOptions", biologicalOptions);
        
        // 작업 특성
        List<Map<String, String>> workCharacteristicsOptions = Arrays.asList(
                createOption("고소작업시 추락", "고소작업시 추락"),
                createOption("개구부 추락", "개구부 추락"),
                createOption("사다리 작업시 추락", "사다리 작업시 추락"),
                createOption("정리정돈 불량에 의한 전도", "정리정돈 불량에 의한 전도"),
                createOption("동일 평면상의 전도", "동일 평면상의 전도"),
                createOption("운반작업 중 맞음", "운반작업 중 맞음"),
                createOption("연결작업 불량", "연결작업 불량"),
                createOption("고온에 의한 화재・화상", "고온에 의한 화재・화상"),
                createOption("작업통로 미확보", "작업통로 미확보"),
                createOption("고열", "고열"),
                createOption("유해광선", "유해광선")
        );
        model.addAttribute("workCharacteristicsOptions", workCharacteristicsOptions);
        
        // 작업환경
        List<Map<String, String>> workEnvironmentOptions = Arrays.asList(
                createOption("소음", "소음"),
                createOption("진동", "진동"),
                createOption("환기", "환기"),
                createOption("조도", "조도"),
                createOption("온열환경", "온열환경"),
                createOption("근골격계 부담작업", "근골격계 부담작업"),
                createOption("무질서/직무스트레스", "무질서/직무스트레스")
        );
        model.addAttribute("workEnvironmentOptions", workEnvironmentOptions);
    }
    
    private Map<String, String> createOption(String value, String text) {
        Map<String, String> option = new HashMap<>();
        option.put("value", value);
        option.put("text", text);
        return option;
    }
    
    // 위험요인 목록 페이지
    @GetMapping("/list")
    public String listRiskFactors(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
        
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);
        
        // 위험요인 목록 가져오기
        List<RiskFactorDto> riskFactors = generalRiskFactorService.getRiskFactorsByConstructionId(constructionId);
        model.addAttribute("riskFactors", riskFactors);
        
        return "pages/general/risk-factor/list";
    }
    
    // 위험요인 상세 페이지
    @GetMapping("/detail/{id}")
    public String detailRiskFactor(@PathVariable("id") Long id, Model model, 
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
        
        Construction construction = generalConstructionService.getConstruction(constructionId);
        model.addAttribute("construction", construction);
        
        // 현장에 해당하는 메뉴 데이터 로드
        List<CustomMenu> customMenus = generalCustomMenuService.getAllMenusByConstructionId(constructionId);
        model.addAttribute("customMenus", customMenus);
        
        // 위험요인 상세 정보 가져오기
        RiskFactorDto riskFactor = generalRiskFactorService.getRiskFactorById(id);
        model.addAttribute("riskFactor", riskFactor);
        
        return "pages/general/risk-factor/detail";
    }
    
    // 위험요인 삭제
    @PostMapping("/delete/{id}")
    public String deleteRiskFactor(@PathVariable("id") Long id, RedirectAttributes redirectAttributes,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 위험요인 삭제
        generalRiskFactorService.deleteRiskFactor(id);
        
        // 리다이렉트 시 메시지 추가
        redirectAttributes.addFlashAttribute("message", "위험요인이 성공적으로 삭제되었습니다.");
        
        return "redirect:/general/risk-factor/list";
    }
}
