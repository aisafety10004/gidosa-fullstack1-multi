package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import net.gidosa.rdb.repositories.mysql.jpa.RequestConstructionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.NoSuchElementException;

@Log4j2
@Service
@RequiredArgsConstructor
public class RequestService {
    
    private final RequestConstructionJpaRepository requestConstructionRepository;
    private final FileAttachmentService fileAttachmentService;
    
    @Transactional(readOnly = true)
    public Page<RequestConstruction> getRequestConstructions(Pageable pageable) {
        return requestConstructionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<RequestConstruction> searchRequestConstructions(String searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return getRequestConstructions(pageable);
        }

        // 정렬은 pageable에서 처리하므로 기존 메서드를 사용
        // 기존 메서드는 ORDER BY r.id DESC가 포함되어 있지만, pageable의 정렬이 우선 적용됨
        switch (searchType) {
            case "name":
                return requestConstructionRepository.findByNameContaining(searchKeyword, pageable);
            case "phone":
                return requestConstructionRepository.findByPhoneContaining(searchKeyword, pageable);
            case "location":
                return requestConstructionRepository.findByConstructionLocationContaining(searchKeyword, pageable);
            default:
                return getRequestConstructions(pageable);
        }
    }
    
    // 상세 조회 메소드 추가
    @Transactional(readOnly = true)
    public RequestConstruction getRequestConstructionById(Long id) {
        // 첨부 파일을 함께 로드하는 JPQL 쿼리 메서드 사용
        RequestConstruction requestConstruction = requestConstructionRepository.findByIdWithAttachments(id);
        if (requestConstruction == null) {
            throw new NoSuchElementException("ID가 " + id + "인 건설 문의를 찾을 수 없습니다.");
        }
        return requestConstruction;
    }
    
    // 수정 메소드 추가
    @Transactional
    public RequestConstruction updateRequestConstruction(Long id, RequestConstruction updatedRequest) {
        // 첨부 파일을 포함한 모든 정보를 로드
        RequestConstruction existingRequest = requestConstructionRepository.findByIdWithAttachments(id);
        if (existingRequest == null) {
            throw new NoSuchElementException("ID가 " + id + "인 건설 문의를 찾을 수 없습니다.");
        }
        
        // 수정 가능한 필드들만 업데이트
        existingRequest.setName(updatedRequest.getName());
        existingRequest.setPhone(updatedRequest.getPhone());
        existingRequest.setConstructionLocation(updatedRequest.getConstructionLocation());
        existingRequest.setPosition(updatedRequest.getPosition());
        existingRequest.setMessage(updatedRequest.getMessage());
        
        return requestConstructionRepository.save(existingRequest);
    }
    
    /**
     * 첨부 파일 업데이트 처리 메소드
     * @param id 요청 ID
     * @param businessCardFile 새로운 명함 파일
     * @param businessLicenseFile 새로운 사업자등록증 파일
     * @param insuranceCertificateFile 새로운 고용산재보험가입증명원 파일
     * @param deleteBusinessCard 명함 파일 삭제 여부
     * @param deleteBusinessLicense 사업자등록증 파일 삭제 여부
     * @param deleteInsuranceCertificate 고용산재보험가입증명원 파일 삭제 여부
     * @return 업데이트된 RequestConstruction
     */
    @Transactional
    public RequestConstruction updateRequestConstructionFiles(
            Long id, 
            MultipartFile businessCardFile, 
            MultipartFile businessLicenseFile,
            MultipartFile insuranceCertificateFile,
            boolean deleteBusinessCard,
            boolean deleteBusinessLicense,
            boolean deleteInsuranceCertificate) {
        
        // 기존 요청 조회
        RequestConstruction existingRequest = requestConstructionRepository.findByIdWithAttachments(id);
        if (existingRequest == null) {
            throw new NoSuchElementException("ID가 " + id + "인 건설 문의를 찾을 수 없습니다.");
        }
        
        // 1. 명함 파일 처리
        if (deleteBusinessCard && existingRequest.getBusinessCard() != null) {
            // 파일 삭제 로직 - 파일 시스템에서 실제 파일 삭제
            fileAttachmentService.deleteAttachment(existingRequest.getBusinessCard().getId());
            existingRequest.setBusinessCard(null);
        }
        
        if (businessCardFile != null && !businessCardFile.isEmpty()) {
            // 새 파일 저장 로직
            FileAttachment newFileEntity = fileAttachmentService.saveFile(businessCardFile, "business_card");
            existingRequest.setBusinessCard(newFileEntity);
        }
        
        // 2. 사업자등록증 파일 처리
        if (deleteBusinessLicense && existingRequest.getBusinessLicense() != null) {
            fileAttachmentService.deleteAttachment(existingRequest.getBusinessLicense().getId());
            existingRequest.setBusinessLicense(null);
        }
        
        if (businessLicenseFile != null && !businessLicenseFile.isEmpty()) {
            FileAttachment newFileEntity = fileAttachmentService.saveFile(businessLicenseFile, "business_license");
            existingRequest.setBusinessLicense(newFileEntity);
        }
        
        // 3. 고용산재보험가입증명원 파일 처리
        if (deleteInsuranceCertificate && existingRequest.getInsuranceCertificate() != null) {
            fileAttachmentService.deleteAttachment(existingRequest.getInsuranceCertificate().getId());
            existingRequest.setInsuranceCertificate(null);
        }
        
        if (insuranceCertificateFile != null && !insuranceCertificateFile.isEmpty()) {
            FileAttachment newFileEntity = fileAttachmentService.saveFile(insuranceCertificateFile, "insurance_certificate");
            existingRequest.setInsuranceCertificate(newFileEntity);
        }
        
        // 저장 및 반환
        return requestConstructionRepository.save(existingRequest);
    }
} 