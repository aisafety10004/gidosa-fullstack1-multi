package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.InquiryAdmin;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.InquiryAdminJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InquiryAdminService {

    private final InquiryAdminJpaRepository inquiryAdminJpaRepository;
    private final ConstructionJpaRepository constructionJpaRepository;
    private final FileAttachmentJpaRepository fileAttachmentJpaRepository;
    private final String FILE_UPLOAD_PATH = "uploads/inquiry/";

    // 문의사항 목록 조회
    @Transactional(readOnly = true)
    public Page<InquiryAdmin> getInquiriesByConstructionId(Long constructionId, Pageable pageable) {
        return inquiryAdminJpaRepository.findByConstructionIdOrderByIdDesc(constructionId, pageable);
    }

    // 문의사항 검색
    @Transactional(readOnly = true)
    public Page<InquiryAdmin> searchInquiries(Long constructionId, String searchTitle, String searchDateRange, String inquiryType, Boolean answered, Pageable pageable) {
        if(constructionId == null) {
            return inquiryAdminJpaRepository.findAllWithConstructionOrderByIdDesc(pageable);
        }

        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            // 제목 검색 조건이 있는 경우
            if (searchDateRange != null && !searchDateRange.trim().isEmpty()) {
                // 날짜 범위도 있는 경우
                String[] dateRange = searchDateRange.split(" ~ ");
                LocalDateTime startDate = LocalDateTime.parse(dateRange[0] + "T00:00:00");
                LocalDateTime endDate = LocalDateTime.parse(dateRange[1] + "T23:59:59");
                return inquiryAdminJpaRepository.findByConstructionIdAndTitleContainingIgnoreCaseAndInquiryDateBetween(constructionId, searchTitle, startDate, endDate, pageable);
            } else {
                // 제목만 검색
                return inquiryAdminJpaRepository.findByConstructionIdAndTitleContainingIgnoreCase(constructionId, searchTitle, pageable);
            }
        } else if (searchDateRange != null && !searchDateRange.trim().isEmpty()) {
            // 날짜 범위만 있는 경우
            String[] dateRange = searchDateRange.split(" ~ ");
            LocalDateTime startDate = LocalDateTime.parse(dateRange[0] + "T00:00:00");
            LocalDateTime endDate = LocalDateTime.parse(dateRange[1] + "T23:59:59");
            return inquiryAdminJpaRepository.findByConstructionIdAndInquiryDateBetween(constructionId, startDate, endDate, pageable);
        } else if (inquiryType != null && !inquiryType.trim().isEmpty()) {
            // 문의 유형 검색
            if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                // 제목도 있는 경우
                return inquiryAdminJpaRepository.findByConstructionIdAndInquiryTypeAndTitleContainingIgnoreCase(constructionId, inquiryType, searchTitle, pageable);
            } else {
                // 문의 유형만 검색
                return inquiryAdminJpaRepository.findByConstructionIdAndInquiryType(constructionId, inquiryType, pageable);
            }
        } else if (answered != null) {
            // 답변 여부로 검색
            if (searchTitle != null && !searchTitle.trim().isEmpty()) {
                // 제목도 있는 경우
                return inquiryAdminJpaRepository.findByConstructionIdAndAnsweredAndTitleContainingIgnoreCase(constructionId, answered, searchTitle, pageable);
            } else {
                // 답변 여부만 검색
                return inquiryAdminJpaRepository.findByConstructionIdAndAnswered(constructionId, answered, pageable);
            }
        } else {
            // 검색 조건이 없는 경우 전체 목록 조회
            return inquiryAdminJpaRepository.findByConstructionIdOrderByIdDesc(constructionId, pageable);
        }
    }

    // 문의사항 상세 조회
    @Transactional(readOnly = true)
    public InquiryAdmin getInquiryById(Long id) {
        return inquiryAdminJpaRepository.findByIdWithAttachmentsAndConstruction(id)
                .orElseThrow(() -> new RuntimeException("문의사항을 찾을 수 없습니다. ID: " + id));
    }

    // 문의사항 등록
    @Transactional
    public InquiryAdmin createInquiry(Long constructionId, InquiryAdmin inquiryAdmin, List<MultipartFile> files) throws IOException {
        // 건설현장 조회
        Construction construction = constructionJpaRepository.findById(constructionId)
                .orElseThrow(() -> new RuntimeException("건설현장을 찾을 수 없습니다. ID: " + constructionId));
        
        inquiryAdmin.setConstruction(construction);
        inquiryAdmin.setInquiryDate(LocalDateTime.now());
        inquiryAdmin.setAnswered(false);
        
        // 파일 처리
        if (files != null && !files.isEmpty()) {
            int fileCount = 0;
            for (MultipartFile file : files) {
                if (!file.isEmpty() && fileCount < 3) {
                    FileAttachment attachment = uploadFile(file);
                    
                    if (fileCount == 0) {
                        inquiryAdmin.setFileAttachment1(attachment);
                    } else if (fileCount == 1) {
                        inquiryAdmin.setFileAttachment2(attachment);
                    } else if (fileCount == 2) {
                        inquiryAdmin.setFileAttachment3(attachment);
                    }
                    
                    fileCount++;
                }
            }
        }
        
        return inquiryAdminJpaRepository.save(inquiryAdmin);
    }

    // 문의사항 수정
    @Transactional
    public InquiryAdmin updateInquiry(Long id, InquiryAdmin inquiryAdminDetails, List<MultipartFile> files) throws IOException {
        InquiryAdmin inquiryAdmin = inquiryAdminJpaRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new RuntimeException("문의사항을 찾을 수 없습니다. ID: " + id));
        
        inquiryAdmin.setTitle(inquiryAdminDetails.getTitle());
        inquiryAdmin.setContent(inquiryAdminDetails.getContent());
        inquiryAdmin.setInquiryType(inquiryAdminDetails.getInquiryType());
        inquiryAdmin.setInquirerName(inquiryAdminDetails.getInquirerName());
        inquiryAdmin.setInquirerEmail(inquiryAdminDetails.getInquirerEmail());
        inquiryAdmin.setInquirerPhone(inquiryAdminDetails.getInquirerPhone());
        inquiryAdmin.setIsPrivate(inquiryAdminDetails.getIsPrivate());
        
        // 파일 처리
        if (files != null && !files.isEmpty()) {
            int fileCount = 0;
            for (MultipartFile file : files) {
                if (!file.isEmpty() && fileCount < 3) {
                    FileAttachment attachment = uploadFile(file);
                    
                    if (fileCount == 0) {
                        // 기존 파일이 있으면 대체
                        if (inquiryAdmin.getFileAttachment1() != null) {
                            fileAttachmentJpaRepository.delete(inquiryAdmin.getFileAttachment1());
                        }
                        inquiryAdmin.setFileAttachment1(attachment);
                    } else if (fileCount == 1) {
                        if (inquiryAdmin.getFileAttachment2() != null) {
                            fileAttachmentJpaRepository.delete(inquiryAdmin.getFileAttachment2());
                        }
                        inquiryAdmin.setFileAttachment2(attachment);
                    } else if (fileCount == 2) {
                        if (inquiryAdmin.getFileAttachment3() != null) {
                            fileAttachmentJpaRepository.delete(inquiryAdmin.getFileAttachment3());
                        }
                        inquiryAdmin.setFileAttachment3(attachment);
                    }
                    
                    fileCount++;
                }
            }
        }
        
        return inquiryAdminJpaRepository.save(inquiryAdmin);
    }

    // 문의사항 삭제
    @Transactional
    public void deleteInquiry(Long id) {
        InquiryAdmin inquiryAdmin = inquiryAdminJpaRepository.findByIdWithAttachments(id)
                .orElseThrow(() -> new RuntimeException("문의사항을 찾을 수 없습니다. ID: " + id));
        
        // 첨부 파일 삭제
        if (inquiryAdmin.getFileAttachment1() != null) {
            fileAttachmentJpaRepository.delete(inquiryAdmin.getFileAttachment1());
        }
        if (inquiryAdmin.getFileAttachment2() != null) {
            fileAttachmentJpaRepository.delete(inquiryAdmin.getFileAttachment2());
        }
        if (inquiryAdmin.getFileAttachment3() != null) {
            fileAttachmentJpaRepository.delete(inquiryAdmin.getFileAttachment3());
        }
        
        inquiryAdminJpaRepository.delete(inquiryAdmin);
    }

    // 문의사항 답변 등록/수정
    @Transactional
    public InquiryAdmin answerInquiry(Long id, String answerContent) {
        InquiryAdmin inquiryAdmin = inquiryAdminJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의사항을 찾을 수 없습니다. ID: " + id));
        
        inquiryAdmin.setAnswerContent(answerContent);
        inquiryAdmin.setAnswered(true);
        inquiryAdmin.setAnswerDate(LocalDateTime.now());
        
        return inquiryAdminJpaRepository.save(inquiryAdmin);
    }

    // 파일 업로드 처리
    private FileAttachment uploadFile(MultipartFile file) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(FILE_UPLOAD_PATH);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 파일명 UUID로 변경하여 저장
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String savedFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(savedFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // FileAttachment 엔티티 생성 및 저장
        FileAttachment attachment = new FileAttachment();
        attachment.setOriginalFilename(originalFilename);
        attachment.setStoredFilename(savedFilename);
        attachment.setFilePath(FILE_UPLOAD_PATH + savedFilename);
        attachment.setFileSize(file.getSize());
        attachment.setContentType(file.getContentType());
        
        // 파일 확장자 설정
        if (fileExtension.startsWith(".")) {
            attachment.setFileExtension(fileExtension.substring(1));
        } else {
            attachment.setFileExtension(fileExtension);
        }
        
        return fileAttachmentJpaRepository.save(attachment);
    }
} 