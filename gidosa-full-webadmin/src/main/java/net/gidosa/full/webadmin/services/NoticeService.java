package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
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

@Log4j2
@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeJpaRepository noticeRepository;
    private final FileAttachmentJpaRepository fileAttachmentRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    public Page<Notice> getAllNotices(Pageable pageable) {
        return noticeRepository.findAll(pageable);
    }

    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findByIdWithAttachments(id);
    }

    @Transactional
    public Notice createNotice(Notice notice, List<MultipartFile> files) {
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
                    case 3: notice.setFileAttachment4(attachment); break;
                    case 4: notice.setFileAttachment5(attachment); break;
                }
            }
            
            // 변경된 Notice 다시 저장
            notice = noticeRepository.save(notice);
        }
        
        return notice;
    }

    @Transactional
    public Notice updateNotice(Long id, Notice updatedNotice, List<MultipartFile> newFiles) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        notice.setTitle(updatedNotice.getTitle());
        notice.setContent(updatedNotice.getContent());

        if (newFiles != null && !newFiles.isEmpty()) {
            int existingFiles = countExistingFiles(notice);
            if (existingFiles + newFiles.size() > 5) {
                throw new IllegalArgumentException("첨부파일은 최대 5개까지만 가능합니다.");
            }

            for (MultipartFile file : newFiles) {
                FileAttachment attachment = saveFileAttachment(file);
                addAttachmentToNotice(notice, attachment);
            }
        }

        return noticeRepository.save(notice);
    }

    private FileAttachment saveFileAttachment(MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String storedFilename = generateStoredFilename(originalFilename);
            String filePath = uploadPath + "/" + storedFilename;
            
            File dest = new File(filePath);
            file.transferTo(dest);
            
            FileAttachment attachment = new FileAttachment();
            attachment.setOriginalFilename(originalFilename);
            attachment.setStoredFilename(storedFilename);
            attachment.setContentType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setFilePath(filePath);
            
            return fileAttachmentRepository.save(attachment);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    private void addAttachmentToNotice(Notice notice, FileAttachment attachment) {
        if (notice.getFileAttachment1() == null) {
            notice.setFileAttachment1(attachment);
        } else if (notice.getFileAttachment2() == null) {
            notice.setFileAttachment2(attachment);
        } else if (notice.getFileAttachment3() == null) {
            notice.setFileAttachment3(attachment);
        } else if (notice.getFileAttachment4() == null) {
            notice.setFileAttachment4(attachment);
        } else if (notice.getFileAttachment5() == null) {
            notice.setFileAttachment5(attachment);
        }
    }

    private int countExistingFiles(Notice notice) {
        int count = 0;
        if (notice.getFileAttachment1() != null) count++;
        if (notice.getFileAttachment2() != null) count++;
        if (notice.getFileAttachment3() != null) count++;
        if (notice.getFileAttachment4() != null) count++;
        if (notice.getFileAttachment5() != null) count++;
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
        if (notice.getFileAttachment4() != null) {
            fileAttachmentRepository.findById(notice.getFileAttachment4().getId())
                    .ifPresent(attachments::add);
        }
        if (notice.getFileAttachment5() != null) {
            fileAttachmentRepository.findById(notice.getFileAttachment5().getId())
                    .ifPresent(attachments::add);
        }
        
        return attachments;
    }

    @Transactional
    public void deleteAttachment(Long noticeId, Long attachmentId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));

        // Notice에서 해당 attachment 제거
        if (notice.getFileAttachment1() != null && notice.getFileAttachment1().getId().equals(attachmentId)) {
            notice.setFileAttachment1(null);
        } else if (notice.getFileAttachment2() != null && notice.getFileAttachment2().getId().equals(attachmentId)) {
            notice.setFileAttachment2(null);
        } else if (notice.getFileAttachment3() != null && notice.getFileAttachment3().getId().equals(attachmentId)) {
            notice.setFileAttachment3(null);
        } else if (notice.getFileAttachment4() != null && notice.getFileAttachment4().getId().equals(attachmentId)) {
            notice.setFileAttachment4(null);
        } else if (notice.getFileAttachment5() != null && notice.getFileAttachment5().getId().equals(attachmentId)) {
            notice.setFileAttachment5(null);
        }

        noticeRepository.save(notice);

        // 실제 파일 및 DB에서 FileAttachment 삭제
        FileAttachment attachment = fileAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("첨부파일을 찾을 수 없습니다."));

        try {
            Files.deleteIfExists(Paths.get(attachment.getFilePath()));
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생", e);
        }

        fileAttachmentRepository.delete(attachment);
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
}
