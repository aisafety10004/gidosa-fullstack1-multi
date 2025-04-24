package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.dtos.RiskFactorDto;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.RiskFactorJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 위험요인 서비스
 * 위험요인 등록, 조회, 수정, 삭제 등의 기능 처리
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralRiskFactorService {
    private final RiskFactorJpaRepository riskFactorJpaRepository;
    private final ConstructionJpaRepository constructionJpaRepository;
    private final FileStorageService fileStorageService;

    /**
     * 위험요인 등록
     * @param riskFactorDto 위험요인 DTO
     * @param constructionId 현장 ID
     * @return 저장된 위험요인 ID
     * @throws IOException 파일 업로드 중 발생하는 예외
     */
    @Transactional
    public Long saveRiskFactor(RiskFactorDto riskFactorDto, Long constructionId) throws IOException {
        // 현장 정보 조회
        Construction construction = constructionJpaRepository.findById(constructionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid construction Id:" + constructionId));
        
        // 이미지 파일 처리
        handleImageFile(riskFactorDto);
        
        // DTO를 엔티티로 변환하여 저장
        RiskFactor riskFactor = riskFactorDto.toEntity();
        riskFactor.setConstruction(construction);
        riskFactor.setExecutionDate(LocalDate.now()); // 현재 날짜로 설정
        
        RiskFactor savedRiskFactor = riskFactorJpaRepository.save(riskFactor);
        return savedRiskFactor.getId();
    }
    
    /**
     * 이미지 파일 처리
     * @param riskFactorDto 위험요인 DTO
     * @throws IOException 파일 업로드 중 발생하는 예외
     */
    private void handleImageFile(RiskFactorDto riskFactorDto) throws IOException {
        // 이미지1 처리
        MultipartFile image1File = riskFactorDto.getWorkImage1File();
        if (image1File != null && !image1File.isEmpty()) {
            FileAttachment fileAttachment = fileStorageService.storeFile(image1File, "risk-factor", null);
            if (fileAttachment != null) {
                riskFactorDto.setWorkImage1Url(fileAttachment.getFilePath());
            }
        }
        
        // 이미지2 처리
        MultipartFile image2File = riskFactorDto.getWorkImage2File();
        if (image2File != null && !image2File.isEmpty()) {
            FileAttachment fileAttachment = fileStorageService.storeFile(image2File, "risk-factor", null);
            if (fileAttachment != null) {
                riskFactorDto.setWorkImage2Url(fileAttachment.getFilePath());
            }
        }
    }
    
    /**
     * 현장 ID에 해당하는 위험요인 목록 조회
     * @param constructionId 현장 ID
     * @return 위험요인 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<RiskFactorDto> getRiskFactorsByConstructionId(Long constructionId) {
        // 현장에 해당하는 위험요인 목록 조회
        // ID 기준 내림차순 정렬하여 최근 등록된 위험요인부터 조회
        Pageable pageable = PageRequest.of(0, 1000, Sort.by(Sort.Direction.DESC, "id"));
        Page<RiskFactor> riskFactorPage = riskFactorJpaRepository.findByConstructionId(constructionId, pageable);
        List<RiskFactor> riskFactors = riskFactorPage.getContent();
        
        // 엔티티를 DTO로 변환하여 반환
        return riskFactors.stream()
                .map(RiskFactorDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 위험요인 상세 조회
     * @param id 위험요인 ID
     * @return 위험요인 DTO
     */
    @Transactional(readOnly = true)
    public RiskFactorDto getRiskFactorById(Long id) {
        RiskFactor riskFactor = riskFactorJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid risk factor Id:" + id));
        
        return RiskFactorDto.fromEntity(riskFactor);
    }
    
    /**
     * 위험요인 수정
     * @param id 위험요인 ID
     * @param riskFactorDto 수정할 위험요인 DTO
     * @return 수정된 위험요인 ID
     * @throws IOException 파일 업로드 중 발생하는 예외
     */
    @Transactional
    public Long updateRiskFactor(Long id, RiskFactorDto riskFactorDto) throws IOException {
        // 기존 위험요인 조회
        RiskFactor existingRiskFactor = riskFactorJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid risk factor Id:" + id));
        
        // 이미지 파일 처리
        handleImageFile(riskFactorDto);
        
        // 기존 이미지 URL 유지 (이미지를 변경하지 않은 경우)
        if (riskFactorDto.getWorkImage1Url() == null) {
            riskFactorDto.setWorkImage1Url(existingRiskFactor.getWorkImage1Url());
        }
        if (riskFactorDto.getWorkImage2Url() == null) {
            riskFactorDto.setWorkImage2Url(existingRiskFactor.getWorkImage2Url());
        }
        
        // DTO를 엔티티로 변환하여 업데이트
        RiskFactor riskFactor = riskFactorDto.toEntity();
        riskFactor.setId(id);
        riskFactor.setConstruction(existingRiskFactor.getConstruction());
        riskFactor.setCreatedAt(existingRiskFactor.getCreatedAt());
        
        RiskFactor updatedRiskFactor = riskFactorJpaRepository.save(riskFactor);
        return updatedRiskFactor.getId();
    }
    
    /**
     * 위험요인 삭제
     * @param id 위험요인 ID
     */
    @Transactional
    public void deleteRiskFactor(Long id) {
        riskFactorJpaRepository.deleteById(id);
    }
} 