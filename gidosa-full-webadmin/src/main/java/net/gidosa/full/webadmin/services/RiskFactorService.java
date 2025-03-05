package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.RiskFactorJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class RiskFactorService {

    private final RiskFactorJpaRepository riskFactorRepository;
    private final ConstructionJpaRepository constructionRepository;

    @Transactional(readOnly = true)
    public Page<RiskFactor> getRiskFactorsByConstructionId(Long constructionId, Pageable pageable) {
        return riskFactorRepository.findByConstructionId(constructionId, pageable);
    }

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
    public Optional<RiskFactor> getRiskFactorById(Long id) {
        return riskFactorRepository.findById(id);
    }

    @Transactional
    public RiskFactor saveRiskFactor(RiskFactor riskFactor) {
        return riskFactorRepository.save(riskFactor);
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
        if (riskFactorDetails.getWorkImage1Url() != null) {
            existingRiskFactor.setWorkImage1Url(riskFactorDetails.getWorkImage1Url());
        }
        if (riskFactorDetails.getWorkImage2Url() != null) {
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
        
        return riskFactorRepository.save(existingRiskFactor);
    }

    @Transactional
    public void deleteRiskFactor(Long id) {
        riskFactorRepository.deleteById(id);
    }
} 