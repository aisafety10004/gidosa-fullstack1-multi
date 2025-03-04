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
    public Page<RiskFactor> searchRiskFactors(Long constructionId, String name, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        if (name != null && !name.isEmpty() && startDate != null && endDate != null) {
            return riskFactorRepository.findByConstructionIdAndNameContainingAndDateRange(constructionId, name, startDate, endDate, pageable);
        } else if (name != null && !name.isEmpty()) {
            return riskFactorRepository.findByConstructionIdAndNameContaining(constructionId, name, pageable);
        } else if (startDate != null && endDate != null) {
            return riskFactorRepository.findByConstructionIdAndDateRange(constructionId, startDate, endDate, pageable);
        } else {
            return riskFactorRepository.findByConstructionId(constructionId, pageable);
        }
    }

    @Transactional(readOnly = true)
    public Page<RiskFactor> getRiskFactorsByStatus(Long constructionId, RiskFactor.RiskStatus status, Pageable pageable) {
        return riskFactorRepository.findByConstructionIdAndStatus(constructionId, status, pageable);
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
        
        existingRiskFactor.setName(riskFactorDetails.getName());
        existingRiskFactor.setStartDate(riskFactorDetails.getStartDate());
        existingRiskFactor.setEndDate(riskFactorDetails.getEndDate());
        existingRiskFactor.setDescription(riskFactorDetails.getDescription());
        existingRiskFactor.setStatus(riskFactorDetails.getStatus());
        existingRiskFactor.setLocationDetail(riskFactorDetails.getLocationDetail());
        existingRiskFactor.setRiskLevel(riskFactorDetails.getRiskLevel());
        
        return riskFactorRepository.save(existingRiskFactor);
    }

    @Transactional
    public void deleteRiskFactor(Long id) {
        riskFactorRepository.deleteById(id);
    }
} 