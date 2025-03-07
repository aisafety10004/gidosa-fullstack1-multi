package net.gidosa.full.webadmin.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactorDto {
    
    private Long id;
    
    private String siteName;
    
    private Long constructionId;
    
    // 추가된 필드들
    private String workProcess;
    
    private String workLocation;
    
    private String workImage1Url;
    private MultipartFile workImage1File;
    
    private String workImage2Url;
    private MultipartFile workImage2File;
    
    private RiskFactor.RiskClassification riskClassification;
    
    //private RiskFactor.RiskDetailFactor riskDetailFactor;
    private String riskDetailFactor;
    
    private String riskSituationResult;
    
    private String currentSafetyMeasure;
    
    private byte riskPossibility = 1;
    
    private byte riskCriticality = 1;
    
    private short riskSize = 1;
    
    private RiskFactor.RiskReductionMeasureFirst riskReductionMeasure1;
    
    private String riskReductionMeasure2;
    
    private RiskFactor.RiskMeasureCompletion isRiskMeasureCompletion;
    
    // Convert DTO to Entity
    public RiskFactor toEntity() {
        return RiskFactor.builder()
                .id(id)
                .siteName(siteName)
                // 추가된 필드들
                .workProcess(workProcess)
                .workLocation(workLocation)
                .workImage1Url(workImage1Url)
                .workImage2Url(workImage2Url)
                .riskClassification(riskClassification)
                .riskDetailFactor(riskDetailFactor)
                .riskSituationResult(riskSituationResult)
                .currentSafetyMeasure(currentSafetyMeasure)
                .riskPossibility(riskPossibility)
                .riskCriticality(riskCriticality)
                .riskReductionMeasure1(riskReductionMeasure1)
                .riskReductionMeasure2(riskReductionMeasure2)
                .isRiskMeasureCompletion(isRiskMeasureCompletion)
                .build();
    }
    
    // Convert Entity to DTO
    public static RiskFactorDto fromEntity(RiskFactor riskFactor) {
        return RiskFactorDto.builder()
                .id(riskFactor.getId())
                .siteName(riskFactor.getSiteName())
                .constructionId(riskFactor.getConstruction().getId())
                // 추가된 필드들
                .workProcess(riskFactor.getWorkProcess())
                .workLocation(riskFactor.getWorkLocation())
                .workImage1Url(riskFactor.getWorkImage1Url())
                .workImage2Url(riskFactor.getWorkImage2Url())
                .riskClassification(riskFactor.getRiskClassification())
                .riskDetailFactor(riskFactor.getRiskDetailFactor())
                .riskSituationResult(riskFactor.getRiskSituationResult())
                .currentSafetyMeasure(riskFactor.getCurrentSafetyMeasure())
                .riskPossibility(riskFactor.getRiskPossibility())
                .riskCriticality(riskFactor.getRiskCriticality())
                .riskSize((short)(riskFactor.getRiskPossibility() * riskFactor.getRiskCriticality()))
                .riskReductionMeasure1(riskFactor.getRiskReductionMeasure1())
                .riskReductionMeasure2(riskFactor.getRiskReductionMeasure2())
                .isRiskMeasureCompletion(riskFactor.getIsRiskMeasureCompletion())
                .build();
    }
} 