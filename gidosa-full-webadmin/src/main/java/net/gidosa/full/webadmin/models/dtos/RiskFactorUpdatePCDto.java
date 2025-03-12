package net.gidosa.full.webadmin.models.dtos;

import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RiskFactorUpdatePCDto extends RiskFactorDto {
    
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
    
    private short impRiskSize;
    
    // 개선 관련 필드
    private String impWorkImage1Url;
    private MultipartFile impWorkImage1File;
    
    private String impWorkImage2Url;
    private MultipartFile impWorkImage2File;
    
    private Short impCountCorrectAction;
    
    private Short impCountNoCorrectAction;
    
    private String impNoCorrectContent;
    
    private String impNoCorrectContentPlan;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate impCorrectDate;
    
    private String impCorrectPerson;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate impCorrectCompletionDate;
    
    private String impCorrectConfirmPerson;
    
    private MultipartFile fileAttachment1File;
    private Long fileAttachment1Id;
    private FileAttachment fileAttachment1;
    
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
        riskFactor.setImpWorkImage1Url(impWorkImage1Url);
        riskFactor.setImpWorkImage2Url(impWorkImage2Url);
        riskFactor.setImpCountCorrectAction(impCountCorrectAction);
        riskFactor.setImpCountNoCorrectAction(impCountNoCorrectAction);
        riskFactor.setImpNoCorrectContent(impNoCorrectContent);
        riskFactor.setImpNoCorrectContentPlan(impNoCorrectContentPlan);
        riskFactor.setImpCorrectDate(impCorrectDate);
        riskFactor.setImpCorrectPerson(impCorrectPerson);
        riskFactor.setImpCorrectCompletionDate(impCorrectCompletionDate);
        riskFactor.setImpCorrectConfirmPerson(impCorrectConfirmPerson);
        
        // FileAttachment 처리는 서비스 레이어에서 별도로 처리해야 함
        //riskFactor.setFileAttachment1(fileAttachment1);

        return riskFactor;
    }
    
    // Convert Entity to DTO
    public static RiskFactorUpdatePCDto fromEntity(RiskFactor riskFactor) {
        RiskFactorUpdatePCDto dto = new RiskFactorUpdatePCDto();
        
        // 기본 필드 설정 (부모 클래스의 fromEntity 메서드 활용)
        RiskFactorDto baseDto = RiskFactorDto.fromEntity(riskFactor);
        
        // 부모 클래스의 필드 복사
        dto.setId(baseDto.getId());
        dto.setSiteName(baseDto.getSiteName());
        dto.setConstructionId(baseDto.getConstructionId());
        dto.setWorkProcess(baseDto.getWorkProcess());
        dto.setWorkLocation(baseDto.getWorkLocation());
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
        dto.setImpWorkImage1Url(riskFactor.getImpWorkImage1Url());
        dto.setImpWorkImage2Url(riskFactor.getImpWorkImage2Url());
        dto.setImpCountCorrectAction(riskFactor.getImpCountCorrectAction());
        dto.setImpCountNoCorrectAction(riskFactor.getImpCountNoCorrectAction());
        dto.setImpNoCorrectContent(riskFactor.getImpNoCorrectContent());
        dto.setImpNoCorrectContentPlan(riskFactor.getImpNoCorrectContentPlan());
        dto.setImpCorrectDate(riskFactor.getImpCorrectDate());
        dto.setImpCorrectPerson(riskFactor.getImpCorrectPerson());
        dto.setImpCorrectCompletionDate(riskFactor.getImpCorrectCompletionDate());
        dto.setImpCorrectConfirmPerson(riskFactor.getImpCorrectConfirmPerson());
        
        // FileAttachment 처리
        if (riskFactor.getFileAttachment1() != null) {
            dto.setFileAttachment1Id(riskFactor.getFileAttachment1().getId());
            dto.setFileAttachment1(riskFactor.getFileAttachment1());
        }
        
        return dto;
    }
} 