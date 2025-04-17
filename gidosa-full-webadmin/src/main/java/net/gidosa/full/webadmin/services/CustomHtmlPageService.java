package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.common.constants.CommonConsts;
import net.gidosa.full.webadmin.models.dtos.CustomHtmlPageDTO;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomHtmlPage;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.repositories.mysql.jpa.CustomHtmlPageJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
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
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomHtmlPageService {
    private final CustomHtmlPageJpaRepository customHtmlPageRepository;
    private final FileAttachmentJpaRepository fileAttachmentRepository;

    @Value("${file.upload.path}")
    private String uploadPath;

    // 모든 HTML 페이지 조회
    public Page<CustomHtmlPage> getAllHtmlPages(Pageable pageable) {
        return customHtmlPageRepository.findAll(pageable);
    }

    // ID로 HTML 페이지 조회
    public Optional<CustomHtmlPage> getHtmlPageById(Long id) {
        return customHtmlPageRepository.findByIdWithHtmlFile(id);
    }

    // DTO를 이용한 HTML 페이지 생성
    @Transactional
    public CustomHtmlPage createHtmlPageFromDTO(CustomHtmlPageDTO htmlPageDTO, MultipartFile htmlFile) {
        // DTO를 엔티티로 변환
        CustomHtmlPage htmlPage = new CustomHtmlPage();
        htmlPage.setTitle(htmlPageDTO.getTitle());
        htmlPage.setContent(htmlPageDTO.getContent());
        htmlPage.setPublished(htmlPageDTO.isPublished());
        
        // HTML 파일 처리
        if (htmlFile != null && !htmlFile.isEmpty()) {
            FileAttachment attachment = saveHtmlFileAttachment(htmlFile);
            htmlPage.setHtmlFile(attachment);
        }
        
        // 엔티티 저장
        return customHtmlPageRepository.save(htmlPage);
    }

    // DTO를 이용한 HTML 페이지 수정
    @Transactional
    public CustomHtmlPage updateHtmlPageFromDTO(Long id, CustomHtmlPageDTO htmlPageDTO, MultipartFile htmlFile) {
        // 기존 엔티티 조회
        CustomHtmlPage htmlPage = customHtmlPageRepository.findByIdWithHtmlFile(id)
                .orElseThrow(() -> new RuntimeException("HTML 페이지를 찾을 수 없습니다."));

        // DTO 데이터로 엔티티 업데이트
        htmlPage.setTitle(htmlPageDTO.getTitle());
        htmlPage.setContent(htmlPageDTO.getContent());
        htmlPage.setPublished(htmlPageDTO.isPublished());

        // HTML 파일 처리
        if (htmlFile != null && !htmlFile.isEmpty()) {
            // 기존 파일이 있으면 삭제
            if (htmlPage.getHtmlFile() != null) {
                deleteFileFromStorage(htmlPage.getHtmlFile().getFilePath());
                fileAttachmentRepository.deleteById(htmlPage.getHtmlFile().getId());
            }
            
            // 새 파일 저장
            FileAttachment attachment = saveHtmlFileAttachment(htmlFile);
            htmlPage.setHtmlFile(attachment);
        }

        // 엔티티 저장
        return customHtmlPageRepository.save(htmlPage);
    }

    // HTML 페이지 생성
    @Transactional
    public CustomHtmlPage createHtmlPage(CustomHtmlPage htmlPage, MultipartFile htmlFile) {
        if (htmlFile != null && !htmlFile.isEmpty()) {
            FileAttachment attachment = saveHtmlFileAttachment(htmlFile);
            htmlPage.setHtmlFile(attachment);
        }
        
        return customHtmlPageRepository.save(htmlPage);
    }

    // HTML 페이지 수정
    @Transactional
    public CustomHtmlPage updateHtmlPage(Long id, CustomHtmlPage updatedHtmlPage, MultipartFile htmlFile) {
        CustomHtmlPage htmlPage = customHtmlPageRepository.findByIdWithHtmlFile(id)
                .orElseThrow(() -> new RuntimeException("HTML 페이지를 찾을 수 없습니다."));

        htmlPage.setTitle(updatedHtmlPage.getTitle());
        htmlPage.setContent(updatedHtmlPage.getContent());
        htmlPage.setPublished(updatedHtmlPage.getPublished());

        if (htmlFile != null && !htmlFile.isEmpty()) {
            // 기존 파일이 있으면 삭제
            if (htmlPage.getHtmlFile() != null) {
                deleteFileFromStorage(htmlPage.getHtmlFile().getFilePath());
                fileAttachmentRepository.deleteById(htmlPage.getHtmlFile().getId());
            }
            
            // 새 파일 저장
            FileAttachment attachment = saveHtmlFileAttachment(htmlFile);
            htmlPage.setHtmlFile(attachment);
        }

        return customHtmlPageRepository.save(htmlPage);
    }

    // HTML 페이지 삭제
    @Transactional
    public void deleteHtmlPage(Long id) {
        CustomHtmlPage htmlPage = customHtmlPageRepository.findByIdWithHtmlFile(id)
                .orElseThrow(() -> new RuntimeException("HTML 페이지를 찾을 수 없습니다."));

        // 연결된 파일 삭제
        if (htmlPage.getHtmlFile() != null) {
            deleteFileFromStorage(htmlPage.getHtmlFile().getFilePath());
            fileAttachmentRepository.deleteById(htmlPage.getHtmlFile().getId());
        }

        customHtmlPageRepository.deleteById(id);
    }

    // HTML 파일 첨부 제거
    @Transactional
    public void removeHtmlFile(Long htmlPageId) {
        CustomHtmlPage htmlPage = customHtmlPageRepository.findByIdWithHtmlFile(htmlPageId)
                .orElseThrow(() -> new RuntimeException("HTML 페이지를 찾을 수 없습니다."));

        if (htmlPage.getHtmlFile() != null) {
            deleteFileFromStorage(htmlPage.getHtmlFile().getFilePath());
            fileAttachmentRepository.deleteById(htmlPage.getHtmlFile().getId());
            htmlPage.setHtmlFile(null);
            customHtmlPageRepository.save(htmlPage);
        }
    }

    // HTML 페이지 게시 상태 변경
    @Transactional
    public CustomHtmlPage updatePublishStatus(Long id, boolean publish) {
        CustomHtmlPage htmlPage = customHtmlPageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HTML 페이지를 찾을 수 없습니다."));
        htmlPage.setPublished(publish);
        return customHtmlPageRepository.save(htmlPage);
    }

    // 제목으로 검색
    public Page<CustomHtmlPage> searchHtmlPagesByTitle(String title, Pageable pageable) {
        return customHtmlPageRepository.findByTitleContaining(title, pageable);
    }

    // 게시 상태로 검색
    public Page<CustomHtmlPage> searchHtmlPagesByPublished(Boolean published, Pageable pageable) {
        return customHtmlPageRepository.findByPublished(published, pageable);
    }

    // 날짜 범위로 검색
    public Page<CustomHtmlPage> searchHtmlPagesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return customHtmlPageRepository.findByCreatedAtBetween(startDate, endDate, pageable);
    }

    // 제목 및 날짜 범위로 검색
    public Page<CustomHtmlPage> searchHtmlPagesByTitleAndDateRange(String title, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return customHtmlPageRepository.findByTitleContainingAndCreatedAtBetween(title, startDate, endDate, pageable);
    }

    // 제목 및 게시 상태로 검색
    public Page<CustomHtmlPage> searchHtmlPagesByTitleAndPublished(String title, Boolean published, Pageable pageable) {
        return customHtmlPageRepository.findByTitleContainingAndPublished(title, published, pageable);
    }

    // 날짜 범위 및 게시 상태로 검색
    public Page<CustomHtmlPage> searchHtmlPagesByDateRangeAndPublished(LocalDateTime startDate, LocalDateTime endDate, Boolean published, Pageable pageable) {
        return customHtmlPageRepository.findByCreatedAtBetweenAndPublished(startDate, endDate, published, pageable);
    }

    // 제목, 날짜 범위, 게시 상태로 검색
    public Page<CustomHtmlPage> searchHtmlPagesByTitleAndDateRangeAndPublished(String title, LocalDateTime startDate, LocalDateTime endDate, Boolean published, Pageable pageable) {
        return customHtmlPageRepository.findByTitleContainingAndCreatedAtBetweenAndPublished(title, startDate, endDate, published, pageable);
    }

    // HTML 파일 저장 로직
    private FileAttachment saveHtmlFileAttachment(MultipartFile file) {
        if (file.isEmpty()) {
            return null;
        }

        try {
            // 파일 확장자 검증
            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            if (!extension.equalsIgnoreCase("html") && !extension.equalsIgnoreCase("htm")) {
                throw new IllegalArgumentException("HTML 파일만 업로드할 수 있습니다.");
            }

            // 업로드 디렉토리 생성
            File uploadDir = new File(uploadPath + File.separator + CommonConsts.CUSTOM_HTML_FOLOER);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 저장할 파일명 생성
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
            attachment.setFileExtension(extension);
            attachment.setFileType("html");

            return fileAttachmentRepository.save(attachment);
        } catch (IOException e) {
            log.error("HTML 파일 저장 중 오류 발생", e);
            throw new RuntimeException("HTML 파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    // 파일 확장자 추출
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty() || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    // 파일 저장 이름 생성
    private String generateStoredFilename(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    // 물리적 파일 삭제
    private void deleteFileFromStorage(String filePath) {
        try {
            Files.deleteIfExists(Paths.get(filePath));
        } catch (IOException e) {
            log.error("파일 삭제 중 오류 발생", e);
        }
    }
} 