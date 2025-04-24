package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.NoticeJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Log4j2
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeJpaRepository noticeRepository;
    private final FileAttachmentJpaRepository fileAttachmentRepository;
    private final ConstructionJpaRepository constructionRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    // 모든 공지사항 조회 (ROLE_ADMIN용) - 동적 정렬 지원
    public Page<Notice> getAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable); // JpaRepository의 기본 메서드는 동적 정렬 지원함
    }
    
    // 특정 건설현장 ID에 해당하는 공지사항 또는 모든 공지사항(construction_id가 null인 경우) 조회 (ROLE_MANAGER용)
    public Page<Notice> getNoticesByConstructionId(Long constructionId, Pageable pageable) {
        return noticeRepository.findByConstructionIdOrConstructionIsNullWithConstructionOrderByIdDesc(constructionId, pageable);
    }
    
    // 모든 사용자에게 보이는 공지사항만 조회 (construction_id가 null인 경우) - 동적 정렬 지원
    public Page<Notice> getGlobalNotices(Pageable pageable) {
        return noticeRepository.findGlobalNotices(pageable);
    }

    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findByIdWithAttachmentsAndConstruction(id);
    }

    @Transactional
    public Notice createNotice(Notice notice, List<MultipartFile> files, Long constructionId) {
        if (notice.getNoticeDate() == null) {
            notice.setNoticeDate(LocalDateTime.now());
        }
        
        // Construction 설정
        if (constructionId != null) {
            constructionRepository.findById(constructionId)
                .ifPresent(notice::setConstruction);
        }
        
        // 먼저 Notice를 저장하여 ID를 얻음
        notice = noticeRepository.save(notice);
        
        if (files != null && !files.isEmpty()) {
            if (files.size() > 5) {
                throw new IllegalArgumentException("첨부파일은 최대 5개까지만 가능합니다.");
            }
            
            for (int i = 0; i < files.size(); i++) {
                MultipartFile file = files.get(i);
                FileAttachment attachment = saveFileAttachment(file);
                
                // Notice에 FileAttachment 설정
                switch (i) {
                    case 0: notice.setFileAttachment1(attachment); break;
                    case 1: notice.setFileAttachment2(attachment); break;
                    case 2: notice.setFileAttachment3(attachment); break;
//                    case 3: notice.setFileAttachment4(attachment); break;
//                    case 4: notice.setFileAttachment5(attachment); break;
                }
            }
            
            // 변경된 Notice 다시 저장
            notice = noticeRepository.save(notice);
        }
        
        return notice;
    }
    
    // 기존 createNotice 메서드를 오버로딩하여 하위 호환성 유지
    @Transactional
    public Notice createNotice(Notice notice, List<MultipartFile> files) {
        return createNotice(notice, files, null);
    }

    @Transactional
    public Notice updateNotice(Long id, Notice updatedNotice, List<MultipartFile> files) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        notice.setTitle(updatedNotice.getTitle());
        notice.setContent(updatedNotice.getContent());
        notice.setNoticeDate(updatedNotice.getNoticeDate());
        notice.setMermaidCode(updatedNotice.getMermaidCode());
        
        // Construction 정보 업데이트
        notice.setConstruction(updatedNotice.getConstruction());

        if (files != null && !files.isEmpty()) {
            int existingFileCount = countExistingFiles(notice);
            int newFileCount = files.size();

            if (existingFileCount + newFileCount > 5) {
                throw new IllegalArgumentException("첨부파일은 최대 5개까지만 가능합니다.");
            }

            for (int i = 0; i < newFileCount; i++) {
                MultipartFile file = files.get(i);
                if (!file.isEmpty()) {
                    FileAttachment attachment = saveFileAttachment(file);

                    // 빈 슬롯에 파일 추가
                    if (notice.getFileAttachment1() == null) {
                        notice.setFileAttachment1(attachment);
                    } else if (notice.getFileAttachment2() == null) {
                        notice.setFileAttachment2(attachment);
                    } else if (notice.getFileAttachment3() == null) {
                        notice.setFileAttachment3(attachment);
//                    } else if (notice.getFileAttachment4() == null) {
//                        notice.setFileAttachment4(attachment);
//                    } else if (notice.getFileAttachment5() == null) {
//                        notice.setFileAttachment5(attachment);
                    }
                }
            }
        }

        return noticeRepository.save(notice);
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
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
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

    private int countExistingFiles(Notice notice) {
        int count = 0;
        if (notice.getFileAttachment1() != null) count++;
        if (notice.getFileAttachment2() != null) count++;
        if (notice.getFileAttachment3() != null) count++;
//        if (notice.getFileAttachment4() != null) count++;
//        if (notice.getFileAttachment5() != null) count++;
        return count;
    }

    @Transactional(readOnly = true)
    public List<FileAttachment> getAttachments(Notice notice) {
        List<FileAttachment> attachments = new ArrayList<>();
        
        if (notice.getFileAttachment1() != null) {
            fileAttachmentRepository.findById(notice.getFileAttachment1().getId())
                    .ifPresent(attachments::add);
        }
        if (notice.getFileAttachment2() != null) {
            fileAttachmentRepository.findById(notice.getFileAttachment2().getId())
                    .ifPresent(attachments::add);
        }
        if (notice.getFileAttachment3() != null) {
            fileAttachmentRepository.findById(notice.getFileAttachment3().getId())
                    .ifPresent(attachments::add);
        }
//        if (notice.getFileAttachment4() != null) {
//            fileAttachmentRepository.findById(notice.getFileAttachment4().getId())
//                    .ifPresent(attachments::add);
//        }
//        if (notice.getFileAttachment5() != null) {
//            fileAttachmentRepository.findById(notice.getFileAttachment5().getId())
//                    .ifPresent(attachments::add);
//        }
        
        return attachments;
    }

    @Transactional
    public void removeAttachment(Long noticeId, int attachmentIndex) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        FileAttachment attachment = null;
        switch (attachmentIndex) {
            case 1:
                attachment = notice.getFileAttachment1();
                notice.setFileAttachment1(null);
                break;
            case 2:
                attachment = notice.getFileAttachment2();
                notice.setFileAttachment2(null);
                break;
            case 3:
                attachment = notice.getFileAttachment3();
                notice.setFileAttachment3(null);
                break;
//            case 4:
//                attachment = notice.getFileAttachment4();
//                notice.setFileAttachment4(null);
//                break;
//            case 5:
//                attachment = notice.getFileAttachment5();
//                notice.setFileAttachment5(null);
//                break;
            default:
                throw new IllegalArgumentException("잘못된 첨부파일 인덱스입니다.");
        }

        if (attachment != null) {
            try {
                Files.deleteIfExists(Paths.get(attachment.getFilePath()));
                fileAttachmentRepository.delete(attachment);
            } catch (IOException e) {
                log.error("파일 삭제 중 오류 발생", e);
            }
        }

        noticeRepository.save(notice);
    }

    @Transactional
    public void deleteNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        // 첨부 파일 삭제
        List<FileAttachment> attachments = notice.getAttachments();
        for (FileAttachment attachment : attachments) {
            try {
                Files.deleteIfExists(Paths.get(attachment.getFilePath()));
                fileAttachmentRepository.delete(attachment);
            } catch (IOException e) {
                log.error("파일 삭제 중 오류 발생", e);
            }
        }

        // Notice 엔티티 삭제
        noticeRepository.delete(notice);
    }

    private String generateStoredFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    // 검색 기능 - 제목으로 검색
    public Page<Notice> searchNoticesByTitle(String searchTitle, Pageable pageable) {
        if (searchTitle == null || searchTitle.trim().isEmpty()) {
            return noticeRepository.findAll(pageable);
        }
        return noticeRepository.findByTitleContainingIgnoreCase(searchTitle.trim(), pageable);
    }
    
    // 검색 기능 - 특정 건설현장 공지사항 중 제목으로 검색
    public Page<Notice> searchNoticesByTitleAndConstructionId(String searchTitle, Long constructionId, Pageable pageable) {
        if (searchTitle == null || searchTitle.trim().isEmpty()) {
            return getNoticesByConstructionId(constructionId, pageable);
        }
        return noticeRepository.findUnpublishedManagerByConstructionIdAndTitleContainingIgnoreCase(
            constructionId, searchTitle.trim(), pageable);
    }
    
    // 검색 기능 - 공지일자 범위로 검색
    public Page<Notice> searchNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0); // 과거 기본값
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(1); // 미래 기본값 (오늘 포함)
        }
        return noticeRepository.findByNoticeDateBetween(startDate, endDate, pageable);
    }
    
    // 검색 기능 - 특정 건설현장 공지사항 중 공지일자 범위로 검색
    public Page<Notice> searchNoticesByDateRangeAndConstructionId(LocalDateTime startDate, LocalDateTime endDate, 
                                                                Long constructionId, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0); // 과거 기본값
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(1); // 미래 기본값 (오늘 포함)
        }
        return noticeRepository.findByConstructionIdOrConstructionIsNullAndNoticeDateBetween(
            constructionId, startDate, endDate, pageable);
    }
    
    // 검색 기능 - 제목과 공지일자 범위로 검색
    public Page<Notice> searchNoticesByTitleAndDateRange(String searchTitle, LocalDateTime startDate, 
                                                       LocalDateTime endDate, Pageable pageable) {
        if (searchTitle == null || searchTitle.trim().isEmpty()) {
            return searchNoticesByDateRange(startDate, endDate, pageable);
        }
        
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0); // 과거 기본값
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(1); // 미래 기본값 (오늘 포함)
        }
        
        return noticeRepository.findByTitleContainingIgnoreCaseAndNoticeDateBetween(
            searchTitle.trim(), startDate, endDate, pageable);
    }
    
    // 검색 기능 - 특정 건설현장 공지사항 중 제목과 공지일자 범위로 검색
    public Page<Notice> searchNoticesByTitleAndDateRangeAndConstructionId(String searchTitle, LocalDateTime startDate, 
                                                                        LocalDateTime endDate, Long constructionId, 
                                                                        Pageable pageable) {
        if (searchTitle == null || searchTitle.trim().isEmpty()) {
            return searchNoticesByDateRangeAndConstructionId(startDate, endDate, constructionId, pageable);
        }
        
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0); // 과거 기본값
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusDays(1); // 미래 기본값 (오늘 포함)
        }
        
        return noticeRepository.findByConstructionIdOrConstructionIsNullAndTitleContainingIgnoreCaseAndNoticeDateBetween(
            constructionId, searchTitle.trim(), startDate, endDate, pageable);
    }
    
    // 게시된 공지사항만 조회 (특정 건설현장 또는 전체 공지사항)
    public Page<Notice> getPublishedManagerNoticesByConstructionId(Long constructionId, Pageable pageable) {
        return noticeRepository.findPublishedManagerByConstructionIdOrConstructionIsNull(constructionId, pageable);
    }
    
    // 게시되지 않은 공지사항만 조회 (특정 건설현장만)
    public Page<Notice> getUnpublishedManagerNoticesByConstructionId(Long constructionId, Pageable pageable) {
        return noticeRepository.findUnpublishedManagerByConstructionId(constructionId, pageable);
    }
    
    // 제목으로 게시된 공지사항 검색 (특정 건설현장 또는 전체 공지사항)
    public Page<Notice> searchPublishedManagerNoticesByTitleAndConstructionId(String searchTitle, Long constructionId, Pageable pageable) {
        return noticeRepository.findPublishedManagerByConstructionIdOrConstructionIsNullAndTitleContainingIgnoreCase(constructionId, searchTitle, pageable);
    }
    
    // 공지일자 범위로 게시된 공지사항 검색 (특정 건설현장 또는 전체 공지사항)
    public Page<Notice> searchPublishedManagerNoticesByDateRangeAndConstructionId(LocalDateTime startDate, LocalDateTime endDate, Long constructionId, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(100);
        }
        
        return noticeRepository.findPublishedManagerByConstructionIdOrConstructionIsNullAndNoticeDateBetween(constructionId, startDate, endDate, pageable);
    }
    
    // 제목과 공지일자 범위로 게시된 공지사항 검색 (특정 건설현장 또는 전체 공지사항)
    public Page<Notice> searchPublishedManagerNoticesByTitleAndDateRangeAndConstructionId(String searchTitle, LocalDateTime startDate, LocalDateTime endDate, Long constructionId, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(100);
        }
        
        return noticeRepository.findPublishedManagerByConstructionIdOrConstructionIsNullAndTitleContainingIgnoreCaseAndNoticeDateBetween(constructionId, searchTitle, startDate, endDate, pageable);
    }
    
    // 공지사항 게시 상태 변경
    @Transactional
    public Notice publishManagerNotice(Long id, boolean publish) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        
        notice.setPublishedManager(publish);
        return noticeRepository.save(notice);
    }

    // 검색 기능 - 전역 공지사항 중 제목으로 검색 (construction이 null인 경우만) - 동적 정렬 지원
    public Page<Notice> searchGlobalNoticesByTitle(String searchTitle, Pageable pageable) {
        return noticeRepository.findGlobalNoticesByTitle(searchTitle, pageable);
    }
    
    // 검색 기능 - 전역 공지사항 중 공지일자 범위로 검색 (construction이 null인 경우만) - 동적 정렬 지원
    public Page<Notice> searchGlobalNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(10);
        }
        
        return noticeRepository.findGlobalNoticesByDateRange(startDate, endDate, pageable);
    }
    
    // 검색 기능 - 전역 공지사항 중 제목과 공지일자 범위로 검색 (construction이 null인 경우만) - 동적 정렬 지원
    public Page<Notice> searchGlobalNoticesByTitleAndDateRange(String searchTitle, LocalDateTime startDate, 
                                                             LocalDateTime endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(10);
        }
        
        return noticeRepository.findGlobalNoticesByTitleAndDateRange(searchTitle, startDate, endDate, pageable);
    }

    @Transactional
    public Notice publishAnonymousNotice(Long id, boolean publish) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        
        notice.setPublishedAnonymous(publish);
        return noticeRepository.save(notice);
    }
    
    @Transactional
    public Notice publishLoggedInUserNotice(Long id, boolean publish) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
        
        notice.setPublishedLoggedInUser(publish);
        return noticeRepository.save(notice);
    }

    // 익명 사용자에게 보이는 공지사항 조회 - 동적 정렬 지원
    public Page<Notice> getPublishedAnonymousNotices(Pageable pageable) {
        return noticeRepository.findPublishedAnonymous(pageable);
    }
    
    // 제목으로 익명 사용자에게 보이는 공지사항 검색
    public Page<Notice> searchPublishedAnonymousNoticesByTitle(String searchTitle, Pageable pageable) {
        return noticeRepository.findPublishedAnonymousByTitleContainingIgnoreCase(searchTitle, pageable);
    }
    
    // 공지일자 범위로 익명 사용자에게 보이는 공지사항 검색
    public Page<Notice> searchPublishedAnonymousNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(10);
        }
        
        return noticeRepository.findPublishedAnonymousByNoticeDateBetween(startDate, endDate, pageable);
    }
    
    // 제목과 공지일자 범위로 익명 사용자에게 보이는 공지사항 검색
    public Page<Notice> searchPublishedAnonymousNoticesByTitleAndDateRange(String searchTitle, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(10);
        }
        
        return noticeRepository.findPublishedAnonymousByTitleContainingIgnoreCaseAndNoticeDateBetween(searchTitle, startDate, endDate, pageable);
    }
    
    // 로그인 사용자에게 보이는 공지사항 조회 - 동적 정렬 지원
    public Page<Notice> getPublishedLoggedInUserNotices(Pageable pageable) {
        return noticeRepository.findPublishedLoggedInUser(pageable);
    }
    
    // 제목으로 로그인 사용자에게 보이는 공지사항 검색
    public Page<Notice> searchPublishedLoggedInUserNoticesByTitle(String searchTitle, Pageable pageable) {
        return noticeRepository.findPublishedLoggedInUserByTitleContainingIgnoreCase(searchTitle, pageable);
    }
    
    // 공지일자 범위로 로그인 사용자에게 보이는 공지사항 검색
    public Page<Notice> searchPublishedLoggedInUserNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(10);
        }
        
        return noticeRepository.findPublishedLoggedInUserByNoticeDateBetween(startDate, endDate, pageable);
    }
    
    // 제목과 공지일자 범위로 로그인 사용자에게 보이는 공지사항 검색
    public Page<Notice> searchPublishedLoggedInUserNoticesByTitleAndDateRange(String searchTitle, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        if (startDate == null) {
            startDate = LocalDateTime.of(2000, 1, 1, 0, 0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now().plusYears(10);
        }
        
        return noticeRepository.findPublishedLoggedInUserByTitleContainingIgnoreCaseAndNoticeDateBetween(searchTitle, startDate, endDate, pageable);
    }
}
