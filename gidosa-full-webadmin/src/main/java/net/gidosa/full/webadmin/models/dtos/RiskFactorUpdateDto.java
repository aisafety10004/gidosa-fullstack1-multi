package net.gidosa.full.webadmin.models.dtos;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RiskFactorUpdateDto extends RiskFactorDto {
    
    // 추가 정보 필드 (후 입력)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionDate;
    
    private RiskFactor.RiskFactorEvaluationType riskFactorEvaluationType;
    
    private Long authorityDivisionId;
    
    private RiskFactor.RiskFactorAuthorityDivisionLevel authorityDivisionLevel;
    
    private Long divisionDetailId;
    
    private Double latitude;
    
    private Double longitude;
    
    private String relatedLaw;
    
    private Boolean isRiskReductionMeasure;
    
    private String evaluator1;
    
    private String evaluator2;
    
    // 개선등록 관련 필드
    private String impResult;
    
    private byte impRiskPossibility;
    
    private byte impRiskCriticality;
    
    // Convert DTO to Entity (overriding the parent method)
    @Override
    public RiskFactor toEntity() {
        RiskFactor riskFactor = super.toEntity();
        
        // 추가 정보 필드 설정
        riskFactor.setExecutionDate(executionDate);
        riskFactor.setRiskFactorEvaluationType(riskFactorEvaluationType);
        riskFactor.setAuthorityDivisionLevel(authorityDivisionLevel);
        riskFactor.setLatitude(latitude);
        riskFactor.setLongitude(longitude);
        riskFactor.setRelatedLaw(relatedLaw);
        riskFactor.setIsRiskReductionMeasure(isRiskReductionMeasure);
        riskFactor.setEvaluator1(evaluator1);
        riskFactor.setEvaluator2(evaluator2);
        
        // 개선등록 관련 필드 설정
        riskFactor.setImpResult(impResult);
        riskFactor.setImpRiskPossibility(impRiskPossibility);
        riskFactor.setImpRiskCriticality(impRiskCriticality);
        
        return riskFactor;
    }
    
    // Convert Entity to DTO
    public static RiskFactorUpdateDto fromEntity(RiskFactor riskFactor) {
        RiskFactorUpdateDto dto = new RiskFactorUpdateDto();
        
        // 기본 필드 설정 (부모 클래스의 fromEntity 메서드 활용)
        RiskFactorDto baseDto = RiskFactorDto.fromEntity(riskFactor);
        
        // 부모 클래스의 필드 복사
        dto.setId(baseDto.getId());
        dto.setSiteName(baseDto.getSiteName());
        dto.setConstructionId(baseDto.getConstructionId());
        dto.setWorkProcess(baseDto.getWorkProcess());
        dto.setWorkLocation(baseDto.getWorkLocation());
//        dto.setWorkImage1Url(Strings.isNullOrEmpty(baseDto.getWorkImage1Url()) ? null : baseDto.getWorkImage1Url());
//        dto.setWorkImage2Url(Strings.isNullOrEmpty(baseDto.getWorkImage2Url()) ? null : baseDto.getWorkImage2Url());
        dto.setWorkImage1Url(baseDto.getWorkImage1Url());
        dto.setWorkImage2Url(baseDto.getWorkImage2Url());
        dto.setRiskClassification(baseDto.getRiskClassification());
        dto.setRiskDetailFactor(baseDto.getRiskDetailFactor());
        dto.setRiskSituationResult(baseDto.getRiskSituationResult());
        dto.setCurrentSafetyMeasure(baseDto.getCurrentSafetyMeasure());
        dto.setRiskPossibility(baseDto.getRiskPossibility());
        dto.setRiskCriticality(baseDto.getRiskCriticality());
        dto.setRiskSize(baseDto.getRiskSize());
        dto.setRiskReductionMeasure1(baseDto.getRiskReductionMeasure1());
        dto.setRiskReductionMeasure2(baseDto.getRiskReductionMeasure2());
        dto.setIsRiskMeasureCompletion(baseDto.getIsRiskMeasureCompletion());
        
        // 추가 정보 필드 설정
        dto.setExecutionDate(riskFactor.getExecutionDate());
        dto.setRiskFactorEvaluationType(riskFactor.getRiskFactorEvaluationType());
        
        if (riskFactor.getAuthorityDivision() != null) {
            dto.setAuthorityDivisionId(Long.valueOf(riskFactor.getAuthorityDivision().getId()));
        }
        
        dto.setAuthorityDivisionLevel(riskFactor.getAuthorityDivisionLevel());
        
        if (riskFactor.getDivisionDetail() != null) {
            dto.setDivisionDetailId(Long.valueOf(riskFactor.getDivisionDetail().getId()));
        }
        
        dto.setLatitude(riskFactor.getLatitude());
        dto.setLongitude(riskFactor.getLongitude());
        dto.setRelatedLaw(riskFactor.getRelatedLaw());
        dto.setIsRiskReductionMeasure(riskFactor.getIsRiskReductionMeasure());
        dto.setEvaluator1(riskFactor.getEvaluator1());
        dto.setEvaluator2(riskFactor.getEvaluator2());
        
        // 개선등록 관련 필드 설정
        dto.setImpResult(riskFactor.getImpResult());
        dto.setImpRiskPossibility(riskFactor.getImpRiskPossibility());
        dto.setImpRiskCriticality(riskFactor.getImpRiskCriticality());
        
        return dto;
    }
} 