package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.*;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType1Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType2Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType3Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType4Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuContentType5Repository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomMenuContentService {
    
    private final CustomMenuContentType1Repository type1Repository;
    private final CustomMenuContentType2Repository type2Repository;
    private final CustomMenuContentType3Repository type3Repository;
    private final CustomMenuContentType4Repository type4Repository;
    private final CustomMenuContentType5Repository type5Repository;
    private final CustomMenuRepository customMenuRepository;
    private final FileAttachmentService fileAttachmentService;
    
    // 파일 업로드 경로 설정
//    private final String UPLOAD_DIR = "uploads/custom-menu";
    @Value("${file.upload.path}")
    private String UPLOAD_DIR;
    
    /**
     * 특정 메뉴의 타입1 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType1 getType1ContentByMenuId(Long menuId) {
        return type1Repository.findByMenuId(menuId).orElse(null);
    }
    
    /**
     * 특정 메뉴와 날짜의 타입2 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType2 getType2ContentByMenuIdAndDate(Long menuId, LocalDate date) {
        return type2Repository.findByMenuIdAndContentDate(menuId, date).orElse(null);
    }
    
    /**
     * 특정 메뉴의 타입3 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType3 getType3ContentByMenuId(Long menuId) {
        return type3Repository.findByMenuId(menuId).orElse(null);
    }
    
    /**
     * 특정 메뉴의 타입4 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType4 getType4ContentByMenuId(Long menuId) {
        return type4Repository.findByMenuIdWithAttachments(menuId).orElse(null);
    }
    
    /**
     * 특정 메뉴의 타입5 컨텐츠를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType5 getType5ContentByMenuId(Long menuId) {
        return type5Repository.findByMenuIdWithAttachments(menuId).orElse(null);
    }
    
    /**
     * 타입1 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType1 saveType1Content(Long menuId, String content) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 1) {
            throw new IllegalArgumentException("Menu is not type 1");
        }
        
        Optional<CustomMenuContentType1> existingContent = type1Repository.findByMenuId(menuId);
        
        CustomMenuContentType1 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setContent(content);
        } else {
            menuContent = CustomMenuContentType1.builder()
                    .menu(menu)
                    .content(content)
                    .build();
        }
        
        return type1Repository.save(menuContent);
    }
    
    /**
     * 타입2 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType2 saveType2Content(Long menuId, String content, LocalDate date) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 2) {
            throw new IllegalArgumentException("Menu is not type 2");
        }
        
        Optional<CustomMenuContentType2> existingContent = type2Repository.findByMenuIdAndContentDate(menuId, date);
        
        CustomMenuContentType2 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setContent(content);
        } else {
            menuContent = CustomMenuContentType2.builder()
                    .menu(menu)
                    .content(content)
                    .contentDate(date)
                    .build();
        }
        
        return type2Repository.save(menuContent);
    }
    
    /**
     * 타입3 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType3 saveType3Content(Long menuId, String mermaidCode) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 3) {
            throw new IllegalArgumentException("Menu is not type 3");
        }
        
        Optional<CustomMenuContentType3> existingContent = type3Repository.findByMenuId(menuId);
        
        CustomMenuContentType3 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setMermaidCode(mermaidCode);
        } else {
            menuContent = CustomMenuContentType3.builder()
                    .menu(menu)
                    .mermaidCode(mermaidCode)
                    .build();
        }
        
        return type3Repository.save(menuContent);
    }
    
    /**
     * 타입4 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType4 saveType4Content(Long menuId, String content, List<MultipartFile> files) throws IOException {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 4) {
            throw new IllegalArgumentException("Menu is not type 4");
        }
        
        Optional<CustomMenuContentType4> existingContent = type4Repository.findByMenuId(menuId);
        
        CustomMenuContentType4 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setContent(content);
        } else {
            menuContent = CustomMenuContentType4.builder()
                    .menu(menu)
                    .content(content)
                    .build();
            
            // 새로 생성된 엔티티를 먼저 저장하여 ID를 할당받음
            menuContent = type4Repository.save(menuContent);
        }
        
        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            // 빈 파일은 제거
            List<MultipartFile> validFiles = files.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .limit(3) // 최대 3개까지만 처리
                    .toList();
            
            // 파일 순서대로 처리
            for (int i = 0; i < validFiles.size(); i++) {
                MultipartFile file = validFiles.get(i);
                FileAttachment attachment = saveFileAttachment(file, menu.getConstruction());
                
                // 파일 번호에 맞게 설정
                if (i == 0) {
                    // 기존 파일이 있으면 삭제
                    if (menuContent.getFileAttachment1() != null) {
                        fileAttachmentService.deleteAttachment(menuContent.getFileAttachment1().getId());
                    }
                    menuContent.setFileAttachment1(attachment);
                } else if (i == 1) {
                    // 기존 파일이 있으면 삭제
                    if (menuContent.getFileAttachment2() != null) {
                        fileAttachmentService.deleteAttachment(menuContent.getFileAttachment2().getId());
                    }
                    menuContent.setFileAttachment2(attachment);
                } else if (i == 2) {
                    // 기존 파일이 있으면 삭제
                    if (menuContent.getFileAttachment3() != null) {
                        fileAttachmentService.deleteAttachment(menuContent.getFileAttachment3().getId());
                    }
                    menuContent.setFileAttachment3(attachment);
                }
            }
        }
        
        return type4Repository.save(menuContent);
    }
    
    /**
     * 타입5 메뉴의 컨텐츠를 저장합니다.
     */
    @Transactional
    public CustomMenuContentType5 saveType5Content(Long menuId, String mermaidCode, List<MultipartFile> files) throws IOException {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 5) {
            throw new IllegalArgumentException("Menu is not type 5");
        }
        
        Optional<CustomMenuContentType5> existingContent = type5Repository.findByMenuId(menuId);
        
        CustomMenuContentType5 menuContent;
        if (existingContent.isPresent()) {
            menuContent = existingContent.get();
            menuContent.setMermaidCode(mermaidCode);
        } else {
            menuContent = CustomMenuContentType5.builder()
                    .menu(menu)
                    .mermaidCode(mermaidCode)
                    .build();
            
            // 새로 생성된 엔티티를 먼저 저장하여 ID를 할당받음
            menuContent = type5Repository.save(menuContent);
        }
        
        // 파일 업로드 처리
        if (files != null && !files.isEmpty()) {
            // 빈 파일은 제거
            List<MultipartFile> validFiles = files.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .limit(3) // 최대 3개까지만 처리
                    .toList();
            
            // 파일 순서대로 처리
            for (int i = 0; i < validFiles.size(); i++) {
                MultipartFile file = validFiles.get(i);
                FileAttachment attachment = saveFileAttachment(file, menu.getConstruction());
                
                // 파일 번호에 맞게 설정
                if (i == 0) {
                    // 기존 파일이 있으면 삭제
                    if (menuContent.getFileAttachment1() != null) {
                        fileAttachmentService.deleteAttachment(menuContent.getFileAttachment1().getId());
                    }
                    menuContent.setFileAttachment1(attachment);
                } else if (i == 1) {
                    // 기존 파일이 있으면 삭제
                    if (menuContent.getFileAttachment2() != null) {
                        fileAttachmentService.deleteAttachment(menuContent.getFileAttachment2().getId());
                    }
                    menuContent.setFileAttachment2(attachment);
                } else if (i == 2) {
                    // 기존 파일이 있으면 삭제
                    if (menuContent.getFileAttachment3() != null) {
                        fileAttachmentService.deleteAttachment(menuContent.getFileAttachment3().getId());
                    }
                    menuContent.setFileAttachment3(attachment);
                }
            }
        }
        
        return type5Repository.save(menuContent);
    }
    
    /**
     * 첨부파일을 저장합니다.
     */
    private FileAttachment saveFileAttachment(MultipartFile file, Construction construction) throws IOException {
        // 원본 파일명에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        
        // 저장 파일명 생성 (UUID + 원본 파일명)
        String storedFilename = UUID.randomUUID() + "_" + originalFilename;
        
        // 파일 저장 경로 설정
        Path uploadDir = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        
        Path filePath = uploadDir.resolve(storedFilename);
        
        // 파일 저장
        Files.copy(file.getInputStream(), filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        // FileAttachment 엔티티 생성 및 저장
        FileAttachment attachment = new FileAttachment();
        attachment.setOriginalFilename(originalFilename);
        attachment.setStoredFilename(storedFilename);
        attachment.setContentType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFilePath(File.separator + filePath);
        attachment.setFileExtension(fileExtension);
        attachment.setConstruction(construction);
        
        return fileAttachmentService.saveAttachment(attachment);
    }
    
    /**
     * 타입1 메뉴의 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType1Content(Long menuId) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 1) {
            throw new IllegalArgumentException("Menu is not type 1");
        }
        
        type1Repository.deleteByMenuId(menuId);
    }
    
    /**
     * 타입2 메뉴의 특정 날짜 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType2Content(Long menuId, LocalDate date) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 2) {
            throw new IllegalArgumentException("Menu is not type 2");
        }
        
        type2Repository.deleteByMenuIdAndContentDate(menuId, date);
    }
    
    /**
     * 타입3 메뉴의 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType3Content(Long menuId) {
        CustomMenu menu = customMenuRepository.findById(menuId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid menu Id: " + menuId));
        
        if (menu.getMenuType() != 3) {
            throw new IllegalArgumentException("Menu is not type 3");
        }
        
        type3Repository.deleteByMenuId(menuId);
    }
    
    /**
     * 타입4 메뉴의 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType4Content(Long menuId) {
        Optional<CustomMenuContentType4> content = type4Repository.findByMenuId(menuId);
        
        if (content.isPresent()) {
            CustomMenuContentType4 menuContent = content.get();
            
            // 첨부파일들 삭제
            if (menuContent.getFileAttachment1() != null) {
                fileAttachmentService.deleteAttachment(menuContent.getFileAttachment1().getId());
            }
            if (menuContent.getFileAttachment2() != null) {
                fileAttachmentService.deleteAttachment(menuContent.getFileAttachment2().getId());
            }
            if (menuContent.getFileAttachment3() != null) {
                fileAttachmentService.deleteAttachment(menuContent.getFileAttachment3().getId());
            }
        }
        
        type4Repository.deleteByMenuId(menuId);
    }
    
    /**
     * 타입5 메뉴의 컨텐츠를 삭제합니다.
     */
    @Transactional
    public void deleteType5Content(Long menuId) {
        Optional<CustomMenuContentType5> content = type5Repository.findByMenuId(menuId);
        
        if (content.isPresent()) {
            CustomMenuContentType5 menuContent = content.get();
            
            // 첨부파일들 삭제
            if (menuContent.getFileAttachment1() != null) {
                fileAttachmentService.deleteAttachment(menuContent.getFileAttachment1().getId());
            }
            if (menuContent.getFileAttachment2() != null) {
                fileAttachmentService.deleteAttachment(menuContent.getFileAttachment2().getId());
            }
            if (menuContent.getFileAttachment3() != null) {
                fileAttachmentService.deleteAttachment(menuContent.getFileAttachment3().getId());
            }
        }
        
        type5Repository.deleteByMenuId(menuId);
    }
    
    /**
     * 첨부파일을 삭제합니다.
     */
    @Transactional
    public void deleteAttachment(Long fileId) {
        fileAttachmentService.deleteAttachment(fileId);
    }

    /**
     * 첨부파일이 포함된 콘텐츠 타입4를 ID로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType4 getType4ContentWithAttachmentsById(Long contentId) {
        return type4Repository.findByIdWithAttachments(contentId).orElse(null);
    }

    /**
     * 첨부파일이 포함된 콘텐츠 타입5를 ID로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType5 getType5ContentWithAttachmentsById(Long contentId) {
        return type5Repository.findByIdWithAttachments(contentId).orElse(null);
    }

    /**
     * 타입1 메뉴 컨텐츠의 익명 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType1 toggleType1PublishedAnonymous(Long contentId) {
        CustomMenuContentType1 content = type1Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedAnonymous(!content.getPublishedAnonymous());
        return type1Repository.save(content);
    }
    
    /**
     * 타입1 메뉴 컨텐츠의 로그인 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType1 toggleType1PublishedLoggedInUser(Long contentId) {
        CustomMenuContentType1 content = type1Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedLoggedInUser(!content.getPublishedLoggedInUser());
        return type1Repository.save(content);
    }
    
    /**
     * 타입2 메뉴 컨텐츠의 익명 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType2 toggleType2PublishedAnonymous(Long contentId) {
        CustomMenuContentType2 content = type2Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedAnonymous(!content.getPublishedAnonymous());
        return type2Repository.save(content);
    }
    
    /**
     * 타입2 메뉴 컨텐츠의 로그인 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType2 toggleType2PublishedLoggedInUser(Long contentId) {
        CustomMenuContentType2 content = type2Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedLoggedInUser(!content.getPublishedLoggedInUser());
        return type2Repository.save(content);
    }
    
    /**
     * 타입3 메뉴 컨텐츠의 익명 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType3 toggleType3PublishedAnonymous(Long contentId) {
        CustomMenuContentType3 content = type3Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedAnonymous(!content.getPublishedAnonymous());
        return type3Repository.save(content);
    }
    
    /**
     * 타입3 메뉴 컨텐츠의 로그인 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType3 toggleType3PublishedLoggedInUser(Long contentId) {
        CustomMenuContentType3 content = type3Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedLoggedInUser(!content.getPublishedLoggedInUser());
        return type3Repository.save(content);
    }
    
    /**
     * 타입4 메뉴 컨텐츠의 익명 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType4 toggleType4PublishedAnonymous(Long contentId) {
        CustomMenuContentType4 content = type4Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedAnonymous(!content.getPublishedAnonymous());
        return type4Repository.save(content);
    }
    
    /**
     * 타입4 메뉴 컨텐츠의 로그인 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType4 toggleType4PublishedLoggedInUser(Long contentId) {
        CustomMenuContentType4 content = type4Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedLoggedInUser(!content.getPublishedLoggedInUser());
        return type4Repository.save(content);
    }
    
    /**
     * 타입5 메뉴 컨텐츠의 익명 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType5 toggleType5PublishedAnonymous(Long contentId) {
        CustomMenuContentType5 content = type5Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedAnonymous(!content.getPublishedAnonymous());
        return type5Repository.save(content);
    }
    
    /**
     * 타입5 메뉴 컨텐츠의 로그인 사용자 공개 여부를 토글합니다.
     */
    @Transactional
    public CustomMenuContentType5 toggleType5PublishedLoggedInUser(Long contentId) {
        CustomMenuContentType5 content = type5Repository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid content Id: " + contentId));
        content.setPublishedLoggedInUser(!content.getPublishedLoggedInUser());
        return type5Repository.save(content);
    }

    /**
     * 타입1 메뉴 컨텐츠를 ID로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType1 getType1ContentById(Long contentId) {
        return type1Repository.findById(contentId).orElse(null);
    }
    
    /**
     * 타입2 메뉴 컨텐츠를 ID로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType2 getType2ContentById(Long contentId) {
        return type2Repository.findById(contentId).orElse(null);
    }
    
    /**
     * 타입3 메뉴 컨텐츠를 ID로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType3 getType3ContentById(Long contentId) {
        return type3Repository.findById(contentId).orElse(null);
    }
    
    /**
     * 타입4 메뉴 컨텐츠를 ID로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType4 getType4ContentById(Long contentId) {
        return type4Repository.findById(contentId).orElse(null);
    }
    
    /**
     * 타입5 메뉴 컨텐츠를 ID로 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenuContentType5 getType5ContentById(Long contentId) {
        return type5Repository.findById(contentId).orElse(null);
    }
} 