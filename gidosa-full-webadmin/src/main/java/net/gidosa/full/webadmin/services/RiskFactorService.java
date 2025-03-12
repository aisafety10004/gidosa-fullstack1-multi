package net.gidosa.full.webadmin.services;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.models.dtos.RiskFactorAdvancedSearchDto;
import net.gidosa.full.webadmin.models.dtos.RiskFactorUpdateDto;
import net.gidosa.full.webadmin.models.dtos.RiskFactorUpdatePCDto;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.RiskFactorJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class RiskFactorService {

    private final RiskFactorJpaRepository riskFactorRepository;
    private final ConstructionJpaRepository constructionRepository;
    private final FileAttachmentJpaRepository fileAttachmentRepository;

    @Transactional(readOnly = true)
    public Page<RiskFactor> searchRiskFactorsByExecutionDate(Long constructionId, String siteName, LocalDate executionDateStart, LocalDate executionDateEnd, Pageable pageable) {
        if (siteName != null && !siteName.isEmpty() && executionDateStart != null && executionDateEnd != null) {
            return riskFactorRepository.findByConstructionIdAndNameContainingAndExecutionDateRange(constructionId, siteName, executionDateStart, executionDateEnd, pageable);
        } else if (siteName != null && !siteName.isEmpty()) {
            return riskFactorRepository.findByConstructionIdAndNameContaining(constructionId, siteName, pageable);
        } else if (executionDateStart != null && executionDateEnd != null) {
            return riskFactorRepository.findByConstructionIdAndExecutionDateRange(constructionId, executionDateStart, executionDateEnd, pageable);
        } else {
            return riskFactorRepository.findByConstructionId(constructionId, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<RiskFactor> advancedSearchWithDto(Long constructionId, RiskFactorAdvancedSearchDto searchDto, Pageable pageable) {
        return riskFactorRepository.advancedSearch(
            constructionId, 
            searchDto.getSiteName(), 
            searchDto.getExecutionDateStart(), 
            searchDto.getExecutionDateEnd(), 
            searchDto.getWorkProcess(), 
            searchDto.getWorkLocation(), 
            searchDto.getRiskClassification(), 
            searchDto.getRiskDetailFactor(),
            searchDto.getImpResult(),
            pageable
        );
    }

    @Transactional(readOnly = true)
    public Optional<RiskFactor> getRiskFactorById(Long id) {
        return riskFactorRepository.findById(id);
    }

    @Transactional
    public RiskFactor createRiskFactor(Long constructionId, RiskFactor riskFactor) {
        Construction construction = constructionRepository.findById(constructionId)
                .orElseThrow(() -> new IllegalArgumentException("Construction not found with ID: " + constructionId));
        
        riskFactor.setConstruction(construction);
        return riskFactorRepository.save(riskFactor);
    }

    @Transactional
    public RiskFactor updateRiskFactor(Long id, RiskFactor riskFactorDetails) {
        RiskFactor existingRiskFactor = riskFactorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Risk factor not found with ID: " + id));
        
        // 기본 정보 업데이트
        existingRiskFactor.setSiteName(riskFactorDetails.getSiteName());

        // 추가 필드 업데이트
        existingRiskFactor.setWorkProcess(riskFactorDetails.getWorkProcess());
        existingRiskFactor.setWorkLocation(riskFactorDetails.getWorkLocation());
        
        // 이미지 URL은 null이 아닌 경우에만 업데이트 (파일 업로드 처리를 위해)
        if (!Strings.isNullOrEmpty(riskFactorDetails.getWorkImage1Url())) {
            existingRiskFactor.setWorkImage1Url(riskFactorDetails.getWorkImage1Url());
        }
        if (!Strings.isNullOrEmpty(riskFactorDetails.getWorkImage2Url())) {
            existingRiskFactor.setWorkImage2Url(riskFactorDetails.getWorkImage2Url());
        }
        
        existingRiskFactor.setRiskClassification(riskFactorDetails.getRiskClassification());
        existingRiskFactor.setRiskDetailFactor(riskFactorDetails.getRiskDetailFactor());
        existingRiskFactor.setRiskSituationResult(riskFactorDetails.getRiskSituationResult());
        existingRiskFactor.setCurrentSafetyMeasure(riskFactorDetails.getCurrentSafetyMeasure());
        existingRiskFactor.setRiskPossibility(riskFactorDetails.getRiskPossibility());
        existingRiskFactor.setRiskCriticality(riskFactorDetails.getRiskCriticality());
        
        existingRiskFactor.setRiskReductionMeasure1(riskFactorDetails.getRiskReductionMeasure1());
        existingRiskFactor.setRiskReductionMeasure2(riskFactorDetails.getRiskReductionMeasure2());
        existingRiskFactor.setIsRiskMeasureCompletion(riskFactorDetails.getIsRiskMeasureCompletion());
        
        // 추가 정보 필드 업데이트 (후 입력)
        existingRiskFactor.setExecutionDate(riskFactorDetails.getExecutionDate());
        existingRiskFactor.setRiskFactorEvaluationType(riskFactorDetails.getRiskFactorEvaluationType());
        existingRiskFactor.setAuthorityDivision(riskFactorDetails.getAuthorityDivision());
        existingRiskFactor.setAuthorityDivisionLevel(riskFactorDetails.getAuthorityDivisionLevel());
        existingRiskFactor.setDivisionDetail(riskFactorDetails.getDivisionDetail());
        existingRiskFactor.setLatitude(riskFactorDetails.getLatitude());
        existingRiskFactor.setLongitude(riskFactorDetails.getLongitude());
        existingRiskFactor.setRelatedLaw(riskFactorDetails.getRelatedLaw());
        existingRiskFactor.setIsRiskReductionMeasure(riskFactorDetails.getIsRiskReductionMeasure());
        existingRiskFactor.setEvaluator1(riskFactorDetails.getEvaluator1());
        existingRiskFactor.setEvaluator2(riskFactorDetails.getEvaluator2());
        
        // 개선등록 관련 필드 업데이트
        existingRiskFactor.setImpResult(riskFactorDetails.getImpResult());
        existingRiskFactor.setImpRiskPossibility(riskFactorDetails.getImpRiskPossibility());
        existingRiskFactor.setImpRiskCriticality(riskFactorDetails.getImpRiskCriticality());
        
        // 개선 이미지 URL은 null이 아닌 경우에만 업데이트 (파일 업로드 처리를 위해)
        if (!Strings.isNullOrEmpty(riskFactorDetails.getImpWorkImage1Url())) {
            existingRiskFactor.setImpWorkImage1Url(riskFactorDetails.getImpWorkImage1Url());
        }
        if (!Strings.isNullOrEmpty(riskFactorDetails.getImpWorkImage2Url())) {
            existingRiskFactor.setImpWorkImage2Url(riskFactorDetails.getImpWorkImage2Url());
        }
        
        // 개선조치 관련 정보 업데이트
        existingRiskFactor.setImpCountCorrectAction(riskFactorDetails.getImpCountCorrectAction());
        existingRiskFactor.setImpCountNoCorrectAction(riskFactorDetails.getImpCountNoCorrectAction());
        existingRiskFactor.setImpNoCorrectContent(riskFactorDetails.getImpNoCorrectContent());
        existingRiskFactor.setImpNoCorrectContentPlan(riskFactorDetails.getImpNoCorrectContentPlan());
        existingRiskFactor.setImpCorrectDate(riskFactorDetails.getImpCorrectDate());
        existingRiskFactor.setImpCorrectPerson(riskFactorDetails.getImpCorrectPerson());
        existingRiskFactor.setImpCorrectCompletionDate(riskFactorDetails.getImpCorrectCompletionDate());
        existingRiskFactor.setImpCorrectConfirmPerson(riskFactorDetails.getImpCorrectConfirmPerson());
        
        // FileAttachment 처리는 별도로 해야 함 (컨트롤러에서 처리)
        
        return riskFactorRepository.save(existingRiskFactor);
    }

    @Transactional
    public void deleteRiskFactor(Long id) {
        riskFactorRepository.deleteById(id);
    }

    @Transactional
    public RiskFactor updateRiskFactorFromUpdateDto(Long id, RiskFactorUpdateDto updateDto) {
        RiskFactor existingRiskFactor = riskFactorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Risk factor not found with ID: " + id));
        
        // 기본 정보 업데이트 (RiskFactorDto 필드)
        existingRiskFactor.setSiteName(updateDto.getSiteName());
        existingRiskFactor.setWorkProcess(updateDto.getWorkProcess());
        existingRiskFactor.setWorkLocation(updateDto.getWorkLocation());
        
        // 이미지 URL은 null이 아닌 경우에만 업데이트 (파일 업로드 처리를 위해)
        if (!Strings.isNullOrEmpty(updateDto.getWorkImage1Url())) {
            existingRiskFactor.setWorkImage1Url(updateDto.getWorkImage1Url());
        }
        if (!Strings.isNullOrEmpty(updateDto.getWorkImage2Url())) {
            existingRiskFactor.setWorkImage2Url(updateDto.getWorkImage2Url());
        }
        
        existingRiskFactor.setRiskClassification(updateDto.getRiskClassification());
        existingRiskFactor.setRiskDetailFactor(updateDto.getRiskDetailFactor());
        existingRiskFactor.setRiskSituationResult(updateDto.getRiskSituationResult());
        existingRiskFactor.setCurrentSafetyMeasure(updateDto.getCurrentSafetyMeasure());
        existingRiskFactor.setRiskPossibility(updateDto.getRiskPossibility());
        existingRiskFactor.setRiskCriticality(updateDto.getRiskCriticality());
        existingRiskFactor.setRiskReductionMeasure1(updateDto.getRiskReductionMeasure1());
        existingRiskFactor.setRiskReductionMeasure2(updateDto.getRiskReductionMeasure2());
        existingRiskFactor.setIsRiskMeasureCompletion(updateDto.getIsRiskMeasureCompletion());
        
        // 추가 정보 필드 업데이트 (후 입력)
        existingRiskFactor.setExecutionDate(updateDto.getExecutionDate());
        existingRiskFactor.setRiskFactorEvaluationType(updateDto.getRiskFactorEvaluationType());
        
        // 관계 엔티티는 컨트롤러에서 처리하도록 함
        existingRiskFactor.setAuthorityDivisionLevel(updateDto.getAuthorityDivisionLevel());
        
        existingRiskFactor.setLatitude(updateDto.getLatitude());
        existingRiskFactor.setLongitude(updateDto.getLongitude());
        existingRiskFactor.setRelatedLaw(updateDto.getRelatedLaw());
        existingRiskFactor.setIsRiskReductionMeasure(updateDto.getIsRiskReductionMeasure());
        existingRiskFactor.setEvaluator1(updateDto.getEvaluator1());
        existingRiskFactor.setEvaluator2(updateDto.getEvaluator2());
        
        // 개선등록 관련 필드 업데이트
        existingRiskFactor.setImpResult(updateDto.getImpResult());
        existingRiskFactor.setImpRiskPossibility(updateDto.getImpRiskPossibility());
        existingRiskFactor.setImpRiskCriticality(updateDto.getImpRiskCriticality());
        
        // 개선 이미지 URL은 null이 아닌 경우에만 업데이트 (파일 업로드 처리를 위해)
        if (!Strings.isNullOrEmpty(updateDto.getImpWorkImage1Url())) {
            existingRiskFactor.setImpWorkImage1Url(updateDto.getImpWorkImage1Url());
        }
        if (!Strings.isNullOrEmpty(updateDto.getImpWorkImage2Url())) {
            existingRiskFactor.setImpWorkImage2Url(updateDto.getImpWorkImage2Url());
        }
        
        // 개선조치 관련 정보 업데이트
        existingRiskFactor.setImpCountCorrectAction(updateDto.getImpCountCorrectAction());
        existingRiskFactor.setImpCountNoCorrectAction(updateDto.getImpCountNoCorrectAction());
        existingRiskFactor.setImpNoCorrectContent(updateDto.getImpNoCorrectContent());
        existingRiskFactor.setImpNoCorrectContentPlan(updateDto.getImpNoCorrectContentPlan());
        existingRiskFactor.setImpCorrectDate(updateDto.getImpCorrectDate());
        existingRiskFactor.setImpCorrectPerson(updateDto.getImpCorrectPerson());
        existingRiskFactor.setImpCorrectCompletionDate(updateDto.getImpCorrectCompletionDate());
        existingRiskFactor.setImpCorrectConfirmPerson(updateDto.getImpCorrectConfirmPerson());
        
        // FileAttachment 처리
        if (updateDto.getFileAttachment1Id() != null) {
            FileAttachment fileAttachment = fileAttachmentRepository.findById(updateDto.getFileAttachment1Id())
                    .orElse(null);
            existingRiskFactor.setFileAttachment1(fileAttachment);
        }
        
        return riskFactorRepository.save(existingRiskFactor);
    }

    @Transactional
    public RiskFactor updateRiskFactorFromUpdatePCDto(Long id, RiskFactorUpdatePCDto updateDto) {
        RiskFactor existingRiskFactor = riskFactorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Risk factor not found with ID: " + id));
        
        // 기본 정보 업데이트 (RiskFactorDto 필드)
        existingRiskFactor.setSiteName(updateDto.getSiteName());
        existingRiskFactor.setWorkProcess(updateDto.getWorkProcess());
        existingRiskFactor.setWorkLocation(updateDto.getWorkLocation());
        
        // 이미지 URL은 null이 아닌 경우에만 업데이트 (파일 업로드 처리를 위해)
        if (!Strings.isNullOrEmpty(updateDto.getWorkImage1Url())) {
            existingRiskFactor.setWorkImage1Url(updateDto.getWorkImage1Url());
        }
        if (!Strings.isNullOrEmpty(updateDto.getWorkImage2Url())) {
            existingRiskFactor.setWorkImage2Url(updateDto.getWorkImage2Url());
        }
        
        existingRiskFactor.setRiskClassification(updateDto.getRiskClassification());
        existingRiskFactor.setRiskDetailFactor(updateDto.getRiskDetailFactor());
        existingRiskFactor.setRiskSituationResult(updateDto.getRiskSituationResult());
        existingRiskFactor.setCurrentSafetyMeasure(updateDto.getCurrentSafetyMeasure());
        existingRiskFactor.setRiskPossibility(updateDto.getRiskPossibility());
        existingRiskFactor.setRiskCriticality(updateDto.getRiskCriticality());
        existingRiskFactor.setRiskReductionMeasure1(updateDto.getRiskReductionMeasure1());
        existingRiskFactor.setRiskReductionMeasure2(updateDto.getRiskReductionMeasure2());
        existingRiskFactor.setIsRiskMeasureCompletion(updateDto.getIsRiskMeasureCompletion());
        
        // 추가 정보 필드 업데이트 (후 입력)
        existingRiskFactor.setExecutionDate(updateDto.getExecutionDate());
        existingRiskFactor.setRiskFactorEvaluationType(updateDto.getRiskFactorEvaluationType());
        
        // 관계 엔티티는 컨트롤러에서 처리하도록 함
        existingRiskFactor.setAuthorityDivisionLevel(updateDto.getAuthorityDivisionLevel());
        
        existingRiskFactor.setLatitude(updateDto.getLatitude());
        existingRiskFactor.setLongitude(updateDto.getLongitude());
        existingRiskFactor.setRelatedLaw(updateDto.getRelatedLaw());
        existingRiskFactor.setIsRiskReductionMeasure(updateDto.getIsRiskReductionMeasure());
        existingRiskFactor.setEvaluator1(updateDto.getEvaluator1());
        existingRiskFactor.setEvaluator2(updateDto.getEvaluator2());
        
        // 개선등록 관련 필드 업데이트
        existingRiskFactor.setImpResult(updateDto.getImpResult());
        existingRiskFactor.setImpRiskPossibility(updateDto.getImpRiskPossibility());
        existingRiskFactor.setImpRiskCriticality(updateDto.getImpRiskCriticality());
        
        // 개선 이미지 URL은 null이 아닌 경우에만 업데이트 (파일 업로드 처리를 위해)
        if (!Strings.isNullOrEmpty(updateDto.getImpWorkImage1Url())) {
            existingRiskFactor.setImpWorkImage1Url(updateDto.getImpWorkImage1Url());
        }
        if (!Strings.isNullOrEmpty(updateDto.getImpWorkImage2Url())) {
            existingRiskFactor.setImpWorkImage2Url(updateDto.getImpWorkImage2Url());
        }
        
        // 개선조치 관련 정보 업데이트
        existingRiskFactor.setImpCountCorrectAction(updateDto.getImpCountCorrectAction());
        existingRiskFactor.setImpCountNoCorrectAction(updateDto.getImpCountNoCorrectAction());
        existingRiskFactor.setImpNoCorrectContent(updateDto.getImpNoCorrectContent());
        existingRiskFactor.setImpNoCorrectContentPlan(updateDto.getImpNoCorrectContentPlan());
        existingRiskFactor.setImpCorrectDate(updateDto.getImpCorrectDate());
        existingRiskFactor.setImpCorrectPerson(updateDto.getImpCorrectPerson());
        existingRiskFactor.setImpCorrectCompletionDate(updateDto.getImpCorrectCompletionDate());
        existingRiskFactor.setImpCorrectConfirmPerson(updateDto.getImpCorrectConfirmPerson());
        
        // FileAttachment 처리
        if (updateDto.getFileAttachment1Id() != null) {
            FileAttachment fileAttachment = fileAttachmentRepository.findById(updateDto.getFileAttachment1Id())
                    .orElse(null);
            existingRiskFactor.setFileAttachment1(fileAttachment);
        }
        
        return riskFactorRepository.save(existingRiskFactor);
    }

    /**
     * Retrieves all risk factors matching the search criteria without pagination
     * Used for exporting data to Excel/CSV
     */
    @Transactional(readOnly = true)
    public List<RiskFactor> getAllRiskFactorsForExport(Long constructionId, RiskFactorAdvancedSearchDto searchDto) {
        return riskFactorRepository.findAllForExport(
            constructionId, 
            searchDto.getSiteName(), 
            searchDto.getExecutionDateStart(), 
            searchDto.getExecutionDateEnd(), 
            searchDto.getWorkProcess(), 
            searchDto.getWorkLocation(), 
            searchDto.getRiskClassification(), 
            searchDto.getRiskDetailFactor(),
            searchDto.getImpResult()
        );
    }
} 