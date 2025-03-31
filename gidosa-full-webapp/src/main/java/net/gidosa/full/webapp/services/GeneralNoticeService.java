package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.NoticeJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralNoticeService {
    private final NoticeJpaRepository noticeRepository;
    private final FileAttachmentJpaRepository fileAttachmentRepository;

    /**
     * 익명 사용자에게 보이는 공지사항 조회
     */
    @Transactional(readOnly = true)
    public Page<Notice> getPublishedAnonymousNotices(Pageable pageable) {
        return noticeRepository.findPublishedAnonymous(pageable);
    }

    /**
     * 로그인 사용자에게 보이는 공지사항 조회
     */
    @Transactional(readOnly = true)
    public Page<Notice> getPublishedLoggedInUserNotices(Pageable pageable) {
        return noticeRepository.findPublishedLoggedInUser(pageable);
    }

    /**
     * 특정 건설현장에 대한 익명 사용자에게 보이는 공지사항 조회
     */
    @Transactional(readOnly = true)
    public Page<Notice> getPublishedAnonymousNoticesByConstructionId(Long constructionId, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findByConstructionIdAndPublishedAnonymous(constructionId, true, pageable);
    }

    /**
     * 특정 건설현장에 대한 로그인 사용자에게 보이는 공지사항 조회
     */
    @Transactional(readOnly = true)
    public Page<Notice> getPublishedLoggedInUserNoticesByConstructionId(Long constructionId, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findByConstructionIdAndPublishedLoggedInUser(constructionId, true, pageable);
    }

    /**
     * 제목으로 익명 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchPublishedAnonymousNoticesByTitle(String searchKeyword, Pageable pageable) {
        return noticeRepository.findPublishedAnonymousByTitleContainingIgnoreCase(searchKeyword, pageable);
    }

    /**
     * 내용으로 익명 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchPublishedAnonymousNoticesByContent(String searchKeyword, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findPublishedAnonymousByContentContainingIgnoreCase(searchKeyword, pageable);
    }

    /**
     * 제목으로 로그인 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchPublishedLoggedInUserNoticesByTitle(String searchKeyword, Pageable pageable) {
        return noticeRepository.findPublishedLoggedInUserByTitleContainingIgnoreCase(searchKeyword, pageable);
    }

    /**
     * 내용으로 로그인 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchPublishedLoggedInUserNoticesByContent(String searchKeyword, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findPublishedLoggedInUserByContentContainingIgnoreCase(searchKeyword, pageable);
    }

    /**
     * 제목으로 특정 건설현장에 대한 익명 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchAnonymousNoticesByTitleAndConstructionId(String searchKeyword, Long constructionId, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findByConstructionIdAndPublishedAnonymousAndTitleContainingIgnoreCase(
                constructionId, true, searchKeyword, pageable);
    }

    /**
     * 내용으로 특정 건설현장에 대한 익명 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchAnonymousNoticesByContentAndConstructionId(String searchKeyword, Long constructionId, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findByConstructionIdAndPublishedAnonymousAndContentContainingIgnoreCase(
                constructionId, true, searchKeyword, pageable);
    }

    /**
     * 제목으로 특정 건설현장에 대한 로그인 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchLoggedInUserNoticesByTitleAndConstructionId(String searchKeyword, Long constructionId, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findByConstructionIdAndPublishedLoggedInUserAndTitleContainingIgnoreCase(
                constructionId, true, searchKeyword, pageable);
    }

    /**
     * 내용으로 특정 건설현장에 대한 로그인 사용자에게 보이는 공지사항 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchLoggedInUserNoticesByContentAndConstructionId(String searchKeyword, Long constructionId, Pageable pageable) {
        // 기존 NoticeJpaRepository에 메서드 추가 필요
        return noticeRepository.findByConstructionIdAndPublishedLoggedInUserAndContentContainingIgnoreCase(
                constructionId, true, searchKeyword, pageable);
    }

    /**
     * 특정 ID의 공지사항 조회
     */
    @Transactional(readOnly = true)
    public Notice getNoticeById(Long id) {
        return noticeRepository.findByIdWithAttachmentsAndConstruction(id)
                .orElseThrow(() -> new RuntimeException("공지사항을 찾을 수 없습니다."));
    }

    /**
     * 공지사항 첨부파일 목록 조회
     */
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
        
        return attachments;
    }
} 