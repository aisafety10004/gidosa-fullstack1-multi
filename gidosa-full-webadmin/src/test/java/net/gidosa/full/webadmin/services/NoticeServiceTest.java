package net.gidosa.full.webadmin.services;

import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.NoticeJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {

    @Mock
    private NoticeJpaRepository noticeRepository;

    @Mock
    private FileAttachmentJpaRepository fileAttachmentRepository;

    @Mock
    private ConstructionJpaRepository constructionRepository;

    @InjectMocks
    private NoticeService noticeService;

    private Notice testNotice;
    private Construction testConstruction;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 설정
        testConstruction = new Construction();
        testConstruction.setId(1L);
        testConstruction.setName("테스트 건설현장");

        testNotice = new Notice();
        testNotice.setId(1L);
        testNotice.setTitle("테스트 공지사항");
        testNotice.setContent("테스트 내용입니다.");
        testNotice.setNoticeDate(LocalDateTime.now());
        testNotice.setConstruction(testConstruction);

        pageable = PageRequest.of(0, 10);

        // 파일 업로드 경로 설정
        ReflectionTestUtils.setField(noticeService, "uploadPath", "test-uploads");
    }

    @Test
    @DisplayName("모든 공지사항 조회 테스트")
    void getAllNoticesTest() {
        // Given
        List<Notice> notices = new ArrayList<>();
        notices.add(testNotice);
        Page<Notice> noticePage = new PageImpl<>(notices, pageable, notices.size());
        
        when(noticeRepository.findAll(pageable)).thenReturn(noticePage);

        // When
        Page<Notice> result = noticeService.getAllNotices(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 공지사항");
        verify(noticeRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("건설현장별 공지사항 조회 테스트")
    void getNoticesByConstructionIdTest() {
        // Given
        List<Notice> notices = new ArrayList<>();
        notices.add(testNotice);
        
        Notice globalNotice = new Notice();
        globalNotice.setId(2L);
        globalNotice.setTitle("전체 공지사항");
        globalNotice.setContent("전체 공지사항 내용");
        globalNotice.setNoticeDate(LocalDateTime.now());
        notices.add(globalNotice);
        
        Page<Notice> noticePage = new PageImpl<>(notices, pageable, notices.size());
        
        when(noticeRepository.findByConstructionIdOrConstructionIsNullWithConstructionOrderByIdDesc(anyLong(), any(Pageable.class)))
                .thenReturn(noticePage);

        // When
        Page<Notice> result = noticeService.getNoticesByConstructionId(1L, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("title")
                .containsExactlyInAnyOrder("테스트 공지사항", "전체 공지사항");
        verify(noticeRepository, times(1))
                .findByConstructionIdOrConstructionIsNullWithConstructionOrderByIdDesc(1L, pageable);
    }

    @Test
    @DisplayName("공지사항 생성 테스트")
    void createNoticeTest() throws IOException {
        // Given
        when(constructionRepository.findById(anyLong())).thenReturn(Optional.of(testConstruction));
        when(noticeRepository.save(any(Notice.class))).thenReturn(testNotice);
        
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "test content".getBytes());
        
        List<MultipartFile> files = new ArrayList<>();
        files.add(file);
        
        // When
        Notice savedNotice = noticeService.createNotice(testNotice, files, 1L);

        // Then
        assertThat(savedNotice).isNotNull();
        assertThat(savedNotice.getTitle()).isEqualTo("테스트 공지사항");
        assertThat(savedNotice.getConstruction()).isEqualTo(testConstruction);
        verify(noticeRepository, times(2)).save(any(Notice.class)); // 첨부파일 저장 후 한번 더 저장
    }

    @Test
    @DisplayName("공지사항 상세 조회 테스트")
    void getNoticeByIdTest() {
        // Given
        when(noticeRepository.findByIdWithAttachmentsAndConstruction(anyLong())).thenReturn(Optional.of(testNotice));

        // When
        Optional<Notice> foundNotice = noticeService.getNoticeById(1L);

        // Then
        assertThat(foundNotice).isPresent();
        assertThat(foundNotice.get().getId()).isEqualTo(1L);
        assertThat(foundNotice.get().getTitle()).isEqualTo("테스트 공지사항");
        verify(noticeRepository, times(1)).findByIdWithAttachmentsAndConstruction(1L);
    }

    @Test
    @DisplayName("공지사항 삭제 테스트")
    void deleteNoticeTest() throws IOException {
        // Given
        FileAttachment fileAttachment = new FileAttachment();
        fileAttachment.setId(1L);
        fileAttachment.setStoredFilename("stored_test.txt");
        fileAttachment.setFilePath("test-uploads/stored_test.txt");
        
        testNotice.setFileAttachment1(fileAttachment);
        
        when(noticeRepository.findById(anyLong())).thenReturn(Optional.of(testNotice));
        doNothing().when(noticeRepository).delete(any(Notice.class));
        doNothing().when(fileAttachmentRepository).delete(any(FileAttachment.class));
        
        // 파일 시스템 모킹
        Path filePath = mock(Path.class);
        when(Paths.get(anyString())).thenReturn(filePath);
        
        // When
        noticeService.deleteNotice(1L);

        // Then
        verify(noticeRepository, times(1)).findById(1L);
        verify(noticeRepository, times(1)).delete(testNotice);
        verify(fileAttachmentRepository, times(1)).delete(fileAttachment);
    }
} 