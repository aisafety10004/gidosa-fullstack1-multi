package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.TestConfig;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
public class NoticeJpaRepositoryTest {

    @Autowired
    private NoticeJpaRepository noticeRepository;

    @Autowired
    private ConstructionJpaRepository constructionRepository;

    @Autowired
    private FileAttachmentJpaRepository fileAttachmentRepository;

    @Test
    @DisplayName("공지사항 저장 및 조회 테스트")
    public void saveAndFindNoticeTest() {
        // Given
        Notice notice = new Notice();
        notice.setTitle("테스트 공지사항");
        notice.setContent("테스트 내용입니다.");
        notice.setNoticeDate(LocalDateTime.now());

        // When
        Notice savedNotice = noticeRepository.save(notice);

        // Then
        assertThat(savedNotice.getId()).isNotNull();
        assertThat(savedNotice.getTitle()).isEqualTo("테스트 공지사항");
        assertThat(savedNotice.getContent()).isEqualTo("테스트 내용입니다.");
    }

    @Test
    @DisplayName("건설현장별 공지사항 조회 테스트")
    public void findByConstructionIdTest() {
        // Given
        Construction construction = new Construction();
        construction.setName("테스트 건설현장");
        Construction savedConstruction = constructionRepository.save(construction);

        Notice notice1 = new Notice();
        notice1.setTitle("건설현장 공지사항");
        notice1.setContent("건설현장 공지사항 내용");
        notice1.setConstruction(savedConstruction);
        notice1.setNoticeDate(LocalDateTime.now());
        noticeRepository.save(notice1);

        Notice notice2 = new Notice();
        notice2.setTitle("전체 공지사항");
        notice2.setContent("전체 공지사항 내용");
        notice2.setConstruction(null); // 전체 공지사항
        notice2.setNoticeDate(LocalDateTime.now());
        noticeRepository.save(notice2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Notice> notices = noticeRepository.findByConstructionIdOrConstructionIsNullOrderByIdDesc(
                savedConstruction.getId(), pageable);

        // Then
        assertThat(notices.getTotalElements()).isEqualTo(2);
        assertThat(notices.getContent()).extracting("title")
                .containsExactlyInAnyOrder("건설현장 공지사항", "전체 공지사항");
    }

    @Test
    @DisplayName("전체 공지사항만 조회 테스트")
    public void findByConstructionIsNullTest() {
        // Given
        Construction construction = new Construction();
        construction.setName("테스트 건설현장");
        Construction savedConstruction = constructionRepository.save(construction);

        Notice notice1 = new Notice();
        notice1.setTitle("건설현장 공지사항");
        notice1.setContent("건설현장 공지사항 내용");
        notice1.setConstruction(savedConstruction);
        notice1.setNoticeDate(LocalDateTime.now());
        noticeRepository.save(notice1);

        Notice notice2 = new Notice();
        notice2.setTitle("전체 공지사항");
        notice2.setContent("전체 공지사항 내용");
        notice2.setConstruction(null); // 전체 공지사항
        notice2.setNoticeDate(LocalDateTime.now());
        noticeRepository.save(notice2);

        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Notice> notices = noticeRepository.findByConstructionIsNullOrderByIdDesc(pageable);

        // Then
        assertThat(notices.getTotalElements()).isEqualTo(1);
        assertThat(notices.getContent().get(0).getTitle()).isEqualTo("전체 공지사항");
    }

    @Test
    @DisplayName("첨부파일이 포함된 공지사항 조회 테스트")
    public void findByIdWithAttachmentsTest() {
        // Given
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setOriginalFilename("test.txt");
        fileAttachment.setStoredFilename("stored_test.txt");
        fileAttachment.setFileSize(1000L);
        FileAttachment savedFileAttachment = fileAttachmentRepository.save(fileAttachment);

        Notice notice = new Notice();
        notice.setTitle("첨부파일 테스트");
        notice.setContent("첨부파일이 있는 공지사항");
        notice.setFileAttachment1(savedFileAttachment);
        notice.setNoticeDate(LocalDateTime.now());
        Notice savedNotice = noticeRepository.save(notice);

        // When
        Optional<Notice> foundNotice = noticeRepository.findByIdWithAttachments(savedNotice.getId());

        // Then
        assertThat(foundNotice).isPresent();
        assertThat(foundNotice.get().getFileAttachment1()).isNotNull();
        assertThat(foundNotice.get().getFileAttachment1().getOriginalFilename()).isEqualTo("test.txt");
        assertThat(foundNotice.get().getAttachments()).hasSize(1);
    }
} 