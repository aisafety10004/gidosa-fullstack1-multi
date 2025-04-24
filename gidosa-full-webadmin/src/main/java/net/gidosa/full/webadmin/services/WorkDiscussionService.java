package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.WorkDiscussion;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.WorkDiscussionJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class WorkDiscussionService {
    private final WorkDiscussionJpaRepository workDiscussionRepository;
    private final FileAttachmentJpaRepository fileAttachmentRepository;
    private final ConstructionJpaRepository constructionRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    // 모든 업무협의 조회 (ROLE_ADMIN용)
    public Page<WorkDiscussion> getAllWorkDiscussions(Pageable pageable) {
        return workDiscussionRepository.findAll(pageable);
    }
    
    // 특정 건설현장 ID에 해당하는 업무협의 또는 모든 업무협의(construction_id가 null인 경우) 조회 (ROLE_MANAGER용)
    public Page<WorkDiscussion> getWorkDiscussionsByConstructionId(Long constructionId, Pageable pageable) {
        return workDiscussionRepository.findByConstructionIdOrConstructionIsNullWithConstructionOrderByIdDesc(constructionId, pageable);
    }
    
    // 모든 사용자에게 보이는 업무협의만 조회 (construction_id가 null인 경우)
    public Page<WorkDiscussion> getGlobalWorkDiscussions(Pageable pageable) {
        return workDiscussionRepository.findByConstructionIsNullOrderByIdDesc(pageable);
    }
    
    // 그룹별 업무협의 조회
    public Page<WorkDiscussion> getWorkDiscussionsByGroup(String groupName, Pageable pageable) {
        return workDiscussionRepository.findByGroupName(groupName, pageable);
    }
    
    // 건설현장 ID와 그룹별 업무협의 조회
    public Page<WorkDiscussion> getWorkDiscussionsByConstructionIdAndGroup(Long constructionId, String groupName, Pageable pageable) {
        return workDiscussionRepository.findByConstructionIdAndGroupName(constructionId, groupName, pageable);
    }

    public Optional<WorkDiscussion> getWorkDiscussionById(Long id) {
        return workDiscussionRepository.findByIdWithConstructionAndAttachments(id);
    }

    @Transactional
    public WorkDiscussion createWorkDiscussion(WorkDiscussion workDiscussion, List<MultipartFile> files, Long constructionId) {
        if (workDiscussion.getDiscussionDate() == null) {
            workDiscussion.setDiscussionDate(LocalDateTime.now());
        }
        
        // Construction 설정
        if (constructionId != null) {
            constructionRepository.findById(constructionId)
                .ifPresent(workDiscussion::setConstruction);
        }
        
        // 먼저 WorkDiscussion을 저장하여 ID를 얻음
        workDiscussion = workDiscussionRepository.save(workDiscussion);
        
        if (files != null && !files.isEmpty()) {
            if (files.size() > 3) {
                throw new IllegalArgumentException("첨부파일은 최대 3개까지만 가능합니다.");
            }
            
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                FileAttachment attachment = saveFileAttachment(file);
                
                // WorkDiscussion에 FileAttachment 설정
                switch (i) {
                    case 0: workDiscussion.setFileAttachment1(attachment); break;
                    case 1: workDiscussion.setFileAttachment2(attachment); break;
                    case 2: workDiscussion.setFileAttachment3(attachment); break;
//                    case 3: workDiscussion.setFileAttachment4(attachment); break;
//                    case 4: workDiscussion.setFileAttachment5(attachment); break;
                }
            }
            
            // 변경된 WorkDiscussion 다시 저장
            workDiscussion = workDiscussionRepository.save(workDiscussion);
        }
        
        return workDiscussion;
    }
    
    // 기존 createWorkDiscussion 메서드를 오버로딩하여 하위 호환성 유지
    @Transactional
    public WorkDiscussion createWorkDiscussion(WorkDiscussion workDiscussion, List<MultipartFile> files) {
        return createWorkDiscussion(workDiscussion, files, null);
    }

    @Transactional
    public WorkDiscussion updateWorkDiscussion(Long id, WorkDiscussion updatedWorkDiscussion, List<MultipartFile> files) {
        WorkDiscussion workDiscussion = workDiscussionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));

        workDiscussion.setTitle(updatedWorkDiscussion.getTitle());
        workDiscussion.setContent(updatedWorkDiscussion.getContent());
        workDiscussion.setDiscussionDate(updatedWorkDiscussion.getDiscussionDate());
        workDiscussion.setMermaidCode(updatedWorkDiscussion.getMermaidCode());
        workDiscussion.setGroupName(updatedWorkDiscussion.getGroupName());
        
        // Construction 정보 업데이트
        workDiscussion.setConstruction(updatedWorkDiscussion.getConstruction());

        if (files != null && !files.isEmpty()) {
            int existingFileCount = countExistingFiles(workDiscussion);
            int newFileCount = files.size();

            if (existingFileCount + newFileCount > 3) {
                throw new IllegalArgumentException("첨부파일은 최대 3개까지만 가능합니다.");
            }

            for (int i = 0; i < newFileCount; i++) {
                MultipartFile file = files.get(i);
                if (!file.isEmpty()) {
                    FileAttachment attachment = saveFileAttachment(file);

                    // 빈 슬롯에 파일 추가
                    if (workDiscussion.getFileAttachment1() == null) {
                        workDiscussion.setFileAttachment1(attachment);
                    } else if (workDiscussion.getFileAttachment2() == null) {
                        workDiscussion.setFileAttachment2(attachment);
                    } else if (workDiscussion.getFileAttachment3() == null) {
                        workDiscussion.setFileAttachment3(attachment);
//                    } else if (workDiscussion.getFileAttachment4() == null) {
//                        workDiscussion.setFileAttachment4(attachment);
//                    } else if (workDiscussion.getFileAttachment5() == null) {
//                        workDiscussion.setFileAttachment5(attachment);
                    }
                }
            }
        }

        return workDiscussionRepository.save(workDiscussion);
    }

    private FileAttachment saveFileAttachment(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // 업로드 디렉토리 생성
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 저장할 파일명 생성
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }

            String storedFilename = generateStoredFilename(originalFilename);
            String filePath = uploadPath + File.separator + storedFilename;

            // 파일 저장
            File dest = new File(filePath);
            file.transferTo(dest);

            // FileAttachment 엔티티 생성 및 저장
            FileAttachment attachment = new FileAttachment();
            attachment.setOriginalFilename(originalFilename);
            attachment.setStoredFilename(storedFilename);
            attachment.setFilePath(filePath);
            attachment.setFileSize(file.getSize());
            attachment.setContentType(file.getContentType());
            attachment.setFileExtension(fileExtension);
//            attachment.setFileType(fileExtension);

            return fileAttachmentRepository.save(attachment);
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생", e);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    private int countExistingFiles(WorkDiscussion workDiscussion) {
        int count = 0;
        if (workDiscussion.getFileAttachment1() != null) count++;
        if (workDiscussion.getFileAttachment2() != null) count++;
        if (workDiscussion.getFileAttachment3() != null) count++;
//        if (workDiscussion.getFileAttachment4() != null) count++;
//        if (workDiscussion.getFileAttachment5() != null) count++;
        return count;
    }

    @Transactional(readOnly = true)
    public List<FileAttachment> getAttachments(WorkDiscussion workDiscussion) {
        List<FileAttachment> attachments = new ArrayList<>();
        
        if (workDiscussion.getFileAttachment1() != null) {
            fileAttachmentRepository.findById(workDiscussion.getFileAttachment1().getId())
                    .ifPresent(attachments::add);
        }
        if (workDiscussion.getFileAttachment2() != null) {
            fileAttachmentRepository.findById(workDiscussion.getFileAttachment2().getId())
                    .ifPresent(attachments::add);
        }
        if (workDiscussion.getFileAttachment3() != null) {
            fileAttachmentRepository.findById(workDiscussion.getFileAttachment3().getId())
                    .ifPresent(attachments::add);
        }
//        if (workDiscussion.getFileAttachment4() != null) {
//            fileAttachmentRepository.findById(workDiscussion.getFileAttachment4().getId())
//                    .ifPresent(attachments::add);
//        }
//        if (workDiscussion.getFileAttachment5() != null) {
//            fileAttachmentRepository.findById(workDiscussion.getFileAttachment5().getId())
//                    .ifPresent(attachments::add);
//        }
        
        return attachments;
    }

    @Transactional
    public void removeAttachment(Long workDiscussionId, int attachmentIndex) {
        WorkDiscussion workDiscussion = workDiscussionRepository.findById(workDiscussionId)
                .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));

        FileAttachment attachment = null;
        switch (attachmentIndex) {
            case 1:
                attachment = workDiscussion.getFileAttachment1();
                workDiscussion.setFileAttachment1(null);
                break;
            case 2:
                attachment = workDiscussion.getFileAttachment2();
                workDiscussion.setFileAttachment2(null);
                break;
            case 3:
                attachment = workDiscussion.getFileAttachment3();
                workDiscussion.setFileAttachment3(null);
                break;
//            case 4:
//                attachment = workDiscussion.getFileAttachment4();
//                workDiscussion.setFileAttachment4(null);
//                break;
//            case 5:
//                attachment = workDiscussion.getFileAttachment5();
//                workDiscussion.setFileAttachment5(null);
//                break;
            default:
                throw new IllegalArgumentException("잘못된 첨부파일 인덱스입니다.");
        }

        if (attachment != null) {
            workDiscussionRepository.save(workDiscussion);
            
            // 파일 시스템에서 파일 삭제
            try {
                Files.deleteIfExists(Paths.get(attachment.getFilePath()));
            } catch (IOException e) {
                log.error("파일 삭제 중 오류 발생", e);
            }
            
            // 데이터베이스에서 FileAttachment 삭제
            fileAttachmentRepository.delete(attachment);
        }
    }

    @Transactional
    public void deleteWorkDiscussion(Long id) {
        WorkDiscussion workDiscussion = workDiscussionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("업무협의를 찾을 수 없습니다."));
        
        // 첨부파일 삭제
        List<FileAttachment> attachments = getAttachments(workDiscussion);
        for (FileAttachment attachment : attachments) {
            try {
                Files.deleteIfExists(Paths.get(attachment.getFilePath()));
            } catch (IOException e) {
                log.error("파일 삭제 중 오류 발생", e);
            }
            fileAttachmentRepository.delete(attachment);
        }
        
        workDiscussionRepository.delete(workDiscussion);
    }

    private String generateStoredFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }
    
    // 제목으로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitle(String searchTitle, Pageable pageable) {
        return workDiscussionRepository.findByTitleContaining(searchTitle, pageable);
    }
    
    // 제목과 그룹으로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitleAndGroup(String searchTitle, String groupName, Pageable pageable) {
        return workDiscussionRepository.findByTitleContainingAndGroupName(searchTitle, groupName, pageable);
    }
    
    // 제목과 건설현장 ID로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitleAndConstructionId(String searchTitle, Long constructionId, Pageable pageable) {
        return workDiscussionRepository.findByTitleContainingAndConstructionId(searchTitle, constructionId, pageable);
    }
    
    // 제목, 건설현장 ID, 그룹으로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitleAndConstructionIdAndGroup(
            String searchTitle, Long constructionId, String groupName, Pageable pageable) {
        return workDiscussionRepository.findByTitleContainingAndConstructionIdAndGroupName(
                searchTitle, constructionId, groupName, pageable);
    }
    
    // 날짜 범위로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByDateRange(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return workDiscussionRepository.findByDiscussionDateBetween(startDate, endDate, pageable);
    }
    
    // 날짜 범위와 그룹으로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByDateRangeAndGroup(
            LocalDateTime startDate, LocalDateTime endDate, String groupName, Pageable pageable) {
        return workDiscussionRepository.findByDiscussionDateBetweenAndGroupName(
                startDate, endDate, groupName, pageable);
    }
    
    // 날짜 범위와 건설현장 ID로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByDateRangeAndConstructionId(
            LocalDateTime startDate, LocalDateTime endDate, Long constructionId, Pageable pageable) {
        return workDiscussionRepository.findByDiscussionDateBetweenAndConstructionId(
                startDate, endDate, constructionId, pageable);
    }
    
    // 날짜 범위, 건설현장 ID, 그룹으로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByDateRangeAndConstructionIdAndGroup(
            LocalDateTime startDate, LocalDateTime endDate, Long constructionId, String groupName, Pageable pageable) {
        return workDiscussionRepository.findByDiscussionDateBetweenAndConstructionIdAndGroupName(
                startDate, endDate, constructionId, groupName, pageable);
    }
    
    // 제목과 날짜 범위로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitleAndDateRange(
            String searchTitle, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return workDiscussionRepository.findByTitleContainingAndDiscussionDateBetween(
                searchTitle, startDate, endDate, pageable);
    }
    
    // 제목, 날짜 범위, 그룹으로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitleAndDateRangeAndGroup(
            String searchTitle, LocalDateTime startDate, LocalDateTime endDate, String groupName, Pageable pageable) {
        return workDiscussionRepository.findByTitleContainingAndDiscussionDateBetweenAndGroupName(
                searchTitle, startDate, endDate, groupName, pageable);
    }
    
    // 제목, 날짜 범위, 건설현장 ID로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitleAndDateRangeAndConstructionId(
            String searchTitle, LocalDateTime startDate, LocalDateTime endDate, Long constructionId, Pageable pageable) {
        return workDiscussionRepository.findByTitleContainingAndDiscussionDateBetweenAndConstructionId(
                searchTitle, startDate, endDate, constructionId, pageable);
    }
    
    // 제목, 날짜 범위, 건설현장 ID, 그룹으로 검색
    public Page<WorkDiscussion> searchWorkDiscussionsByTitleAndDateRangeAndConstructionIdAndGroup(
            String searchTitle, LocalDateTime startDate, LocalDateTime endDate, 
            Long constructionId, String groupName, Pageable pageable) {
        return workDiscussionRepository.findByTitleContainingAndDiscussionDateBetweenAndConstructionIdAndGroupName(
                searchTitle, startDate, endDate, constructionId, groupName, pageable);
    }
} 