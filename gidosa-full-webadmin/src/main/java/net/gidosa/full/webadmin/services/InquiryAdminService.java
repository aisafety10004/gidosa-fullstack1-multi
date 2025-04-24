package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.InquiryAdmin;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.InquiryAdminJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InquiryAdminService {

    private final InquiryAdminJpaRepository inquiryAdminJpaRepository;
    private final ConstructionJpaRepository constructionJpaRepository;
    private final FileAttachmentJpaRepository fileAttachmentJpaRepository;

    @Value("${file.upload.path}")
    private String FILE_UPLOAD_PATH;

    @PersistenceContext
    private EntityManager entityManager;

    // 문의사항 목록 조회
    @Transactional(readOnly = true)
    public Page<InquiryAdmin> getInquiriesByConstructionId(Long constructionId, Pageable pageable) {
        return inquiryAdminJpaRepository.findByConstructionIdOrderByIdDesc(constructionId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<InquiryAdmin> searchInquiries(String searchConstructionName, String searchTitle, String searchDateRange, String inquiryType, Boolean answered, Pageable pageable) {
        // 검색 조건에 따라 적절한 Repository 메소드 호출
        
        // 날짜 범위 처리
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        
        if (searchDateRange != null && !searchDateRange.trim().isEmpty()) {
            try {
                String[] dateRange = searchDateRange.split(" ~ ");
                if (dateRange.length == 2) {
                    startDate = LocalDateTime.parse(dateRange[0] + "T00:00:00");
                    endDate = LocalDateTime.parse(dateRange[1] + "T23:59:59");
                }
            } catch (Exception e) {
                // 날짜 파싱 오류 발생시 무시하고 null로 유지
                // 로그 기록 추가 (운영환경에서는 로그 추가 권장)
                System.err.println("날짜 파싱 오류: " + e.getMessage());
            }
        }
        
        // 일반화된 조건 검색 쿼리 적용 (동적 쿼리 직접 구현)
        // 여러 조건을 직접 조합하는 방식으로 구현
        String jpql = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction c LEFT JOIN FETCH i.fileAttachment1 WHERE 1=1";
        String countJpql = "SELECT COUNT(i) FROM InquiryAdmin i JOIN i.construction c WHERE 1=1"; // 카운트 쿼리는 FETCH 없이
        
        // 파라미터 값을 담을 Map
        Map<String, Object> parameters = new HashMap<>();
        
        // 현장명 조건 추가
        if (searchConstructionName != null && !searchConstructionName.trim().isEmpty()) {
            jpql += " AND LOWER(c.name) LIKE LOWER(:constructionName)";
            countJpql += " AND LOWER(c.name) LIKE LOWER(:constructionName)";
            parameters.put("constructionName", "%" + searchConstructionName.trim() + "%");
        }
        
        // 제목 조건 추가
        if (searchTitle != null && !searchTitle.trim().isEmpty()) {
            jpql += " AND LOWER(i.title) LIKE LOWER(:title)";
            countJpql += " AND LOWER(i.title) LIKE LOWER(:title)";
            parameters.put("title", "%" + searchTitle.trim() + "%");
        }
        
        // 날짜 범위 조건 추가
        if (startDate != null && endDate != null) {
            jpql += " AND i.inquiryDate BETWEEN :startDate AND :endDate";
            countJpql += " AND i.inquiryDate BETWEEN :startDate AND :endDate";
            parameters.put("startDate", startDate);
            parameters.put("endDate", endDate);
        }
        
        // 문의 유형 조건 추가
        if (inquiryType != null && !inquiryType.trim().isEmpty()) {
            jpql += " AND i.inquiryType = :inquiryType";
            countJpql += " AND i.inquiryType = :inquiryType";
            parameters.put("inquiryType", inquiryType);
        }
        
        // 답변 여부 조건 추가
        if (answered != null) {
            jpql += " AND i.answered = :answered";
            countJpql += " AND i.answered = :answered";
            parameters.put("answered", answered);
        }
        
        // 정렬 조건 추가 - 기본적으로 ID 내림차순
        jpql += " ORDER BY i.id DESC";
        
        // 동적 쿼리 실행
        TypedQuery<InquiryAdmin> query = entityManager.createQuery(jpql, InquiryAdmin.class);
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        
        // 파라미터 설정
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
            countQuery.setParameter(entry.getKey(), entry.getValue());
        }
        
        // 페이징 처리
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        
        // 결과 조회
        List<InquiryAdmin> content = query.getResultList();
        Long total = countQuery.getSingleResult();
        
        return new PageImpl<>(content, pageable, total);
    }

    // 문의사항 검색(Manager)
    @Transactional(readOnly = true)
    public Page<InquiryAdmin> searchInquiries(Long constructionId, String searchTitle, String searchDateRange, String inquiryType, Boolean answered, Pageable pageable) {
//         if(constructionId == null) {
//            // 전체 현장에 대한 검색 - 검색 조건 적용
//            if (searchTitle != null && !searchTitle.trim().isEmpty()) {
//                // 제목 검색 조건이 있는 경우
//                if (searchDateRange != null && !searchDateRange.trim().isEmpty()) {
//                    // 날짜 범위도 있는 경우
//                    String[] dateRange = searchDateRange.split(" ~ ");
//                    LocalDateTime startDate = LocalDateTime.parse(dateRange[0] + "T00:00:00");
//                    LocalDateTime endDate = LocalDateTime.parse(dateRange[1] + "T23:59:59");
//                    return inquiryAdminJpaRepository.findAllWithTitleContainingIgnoreCaseAndInquiryDateBetween(searchTitle, startDate, endDate, pageable);
//                } else {
//                    // 제목만 검색
//                    return inquiryAdminJpaRepository.findAllWithTitleContainingIgnoreCase(searchTitle, pageable);
//                }
//            } else if (searchDateRange != null && !searchDateRange.trim().isEmpty()) {
//                // 날짜 범위만 있는 경우
//                String[] dateRange = searchDateRange.split(" ~ ");
//                LocalDateTime startDate = LocalDateTime.parse(dateRange[0] + "T00:00:00");
//                LocalDateTime endDate = LocalDateTime.parse(dateRange[1] + "T23:59:59");
//                return inquiryAdminJpaRepository.findAllWithInquiryDateBetween(startDate, endDate, pageable);
//            } else if (inquiryType != null && !inquiryType.trim().isEmpty()) {
//                // 문의 유형 검색
//                if (searchTitle != null && !searchTitle.trim().isEmpty()) {
//                    // 제목도 있는 경우
//                    return inquiryAdminJpaRepository.findAllWithInquiryTypeAndTitleContainingIgnoreCase(inquiryType, searchTitle, pageable);
//                } else {
//                    // 문의 유형만 검색
//                    return inquiryAdminJpaRepository.findAllWithInquiryType(inquiryType, pageable);
//                }
//            } else if (answered != null) {
//                // 답변 여부로 검색
//                if (searchTitle != null && !searchTitle.trim().isEmpty()) {
//                    // 제목도 있는 경우
//                    return inquiryAdminJpaRepository.findAllWithAnsweredAndTitleContainingIgnoreCase(answered, searchTitle, pageable);
//                } else {
//                    // 답변 여부만 검색
//                    return inquiryAdminJpaRepository.findAllWithAnswered(answered, pageable);
//                }
//            } else {
//                // 검색 조건이 없는 경우 전체 목록 조회
//                return inquiryAdminJpaRepository.findAllWithConstructionOrderByIdDesc(pageable);
//            }
//        }

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
        
        String savedFilename = UUID.randomUUID() + fileExtension;
        Path filePath = uploadPath.resolve(savedFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // FileAttachment 엔티티 생성 및 저장
        FileAttachment attachment = new FileAttachment();
        attachment.setOriginalFilename(originalFilename);
        attachment.setStoredFilename(savedFilename);
        attachment.setFilePath(FILE_UPLOAD_PATH + File.separator + savedFilename);
        attachment.setFileSize(file.getSize());
        attachment.setContentType(file.getContentType());
        attachment.setFileExtension(fileExtension);
//            attachment.setFileType(fileExtension);

        // 파일 확장자 설정
        if (fileExtension.startsWith(".")) {
            attachment.setFileExtension(fileExtension.substring(1));
        } else {
            attachment.setFileExtension(fileExtension);
        }
        
        return fileAttachmentJpaRepository.save(attachment);
    }
} 