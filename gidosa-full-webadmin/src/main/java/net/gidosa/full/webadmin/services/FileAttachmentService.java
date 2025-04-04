package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class FileAttachmentService {
    private final FileAttachmentJpaRepository fileAttachmentJpaRepository;
    
    @Value("${file.upload.path}")
//    @Value("${file.upload.path:uploads/files}")
    private String uploadPath;

    @Transactional(readOnly = true)
    public FileAttachment getFileAttachment(Long id) {
        return fileAttachmentJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
    }

    public Resource getFileResource(FileAttachment attachment) throws IOException {
        Path filePath = Paths.get(attachment.getFilePath());
        return new UrlResource(filePath.toUri());
    }

    @Transactional
    public void deleteAttachment(Long id) {
        FileAttachment attachment = fileAttachmentJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("파일을 찾을 수 없습니다."));
        
        // 실제 파일 삭제
        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 중 오류가 발생했습니다.", e);
        }

        // DB에서 레코드 삭제
        fileAttachmentJpaRepository.delete(attachment);
    }
    
    /**
     * 단일 파일 첨부 정보를 저장합니다.
     */
    @Transactional
    public FileAttachment saveAttachment(FileAttachment attachment) {
        return fileAttachmentJpaRepository.save(attachment);
    }
    
    /**
     * MultipartFile을 저장하고 FileAttachment 엔티티를 생성하여 반환합니다.
     * @param file 저장할 파일
     * @param fileType 파일 타입 정보
     * @return 저장된 FileAttachment 엔티티
     */
    @Transactional
    public FileAttachment saveFile(MultipartFile file, String fileType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("빈 파일입니다.");
            }

            // 현재 날짜 기반 디렉토리 생성
//            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String directoryPath = uploadPath + File.separator + fileType;

            // 파일 저장 경로 생성
            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // 원본 파일명 및 확장자 추출
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }

            // 유니크한 파일명 생성
            String newFilename = UUID.randomUUID() + "." + fileExtension;
            Path filePath = directory.resolve(newFilename);
            
            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // FileAttachment 엔티티 생성 및 저장
            FileAttachment attachment = new FileAttachment();
            attachment.setOriginalFilename(originalFilename);
            attachment.setStoredFilename(newFilename);
            attachment.setFilePath(filePath.toString());
            attachment.setFileSize(file.getSize());
            attachment.setContentType(file.getContentType());
            attachment.setFileType(fileType);
            attachment.setFileExtension(fileExtension);
            
            return saveAttachment(attachment);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}