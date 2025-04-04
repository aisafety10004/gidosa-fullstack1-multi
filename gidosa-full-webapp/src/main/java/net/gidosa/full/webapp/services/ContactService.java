package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.models.dtos.RequestConstructionDto;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.RequestConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.mybatis.ConstructionMyBatisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ConstructionMyBatisRepository constructionRepository;
    private final RequestConstructionJpaRepository requestConstructionRepository;
    private final FileAttachmentJpaRepository fileAttachmentRepository;
    private final FileStorageService fileStorageService;

    public List<Construction> searchByKeyword(String keyword) {
        List<Construction> constructionList = constructionRepository.findByLocationContaining(keyword);
        return constructionList;
    }

    @Transactional
    public RequestConstruction submitRequest(RequestConstructionDto request) {
        RequestConstruction requestConstruction = RequestConstruction.builder()
            .name(request.name())
            .phone(request.phone())
            .constructionLocation(request.constructionLocation())
            .position(request.position())
            .message(request.message())
            .agreement(request.agreement())
            .build();

        processAttachments(request, requestConstruction);
        
        return requestConstructionRepository.save(requestConstruction);
    }
    
    private void processAttachments(RequestConstructionDto request, RequestConstruction requestConstruction) {
        if (request.businessCard() != null && !request.businessCard().isEmpty()) {
            FileAttachment businessCard = storeFile(request.businessCard(), "business_card");
            if (businessCard != null) {
                requestConstruction.setBusinessCard(businessCard);
            }
        }
        
        if (request.businessLicense() != null && !request.businessLicense().isEmpty()) {
            FileAttachment businessLicense = storeFile(request.businessLicense(), "business_license");
            if (businessLicense != null) {
                requestConstruction.setBusinessLicense(businessLicense);
            }
        }
        
        if (request.insuranceCertificate() != null && !request.insuranceCertificate().isEmpty()) {
            FileAttachment insuranceCertificate = storeFile(request.insuranceCertificate(), "insurance_certificate");
            if (insuranceCertificate != null) {
                requestConstruction.setInsuranceCertificate(insuranceCertificate);
            }
        }
    }
    
    private FileAttachment storeFile(MultipartFile file, String fileType) {
        try {
            return fileStorageService.storeFile(file, fileType, null);
        } catch (Exception e) {
            log.error("파일 저장 중 오류 발생: " + e.getMessage(), e);
            return null;
        }
    }
    
    public List<FileAttachment> getRequestFiles(Long requestId) {
        RequestConstruction request = requestConstructionRepository.findById(requestId).orElse(null);
        if (request == null) {
            return new ArrayList<>();
        }
        
        return request.getAttachments();
    }
    
    public FileAttachment getRequestFileByType(Long requestId, String fileType) {
        RequestConstruction request = requestConstructionRepository.findById(requestId).orElse(null);
        if (request == null) {
            return null;
        }
        
        switch (fileType) {
            case "BUSINESS_CARD":
                return request.getBusinessCard();
            case "BUSINESS_LICENSE":
                return request.getBusinessLicense();
            case "INSURANCE_CERTIFICATE":
                return request.getInsuranceCertificate();
            default:
                return null;
        }
    }
}
