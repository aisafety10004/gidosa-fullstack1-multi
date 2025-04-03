package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 파일 저장 서비스
 * 파일 업로드, 저장 관련 기능 처리
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileAttachmentJpaRepository fileAttachmentRepository;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    /**
     * 파일 저장 및 FileAttachment 엔티티 생성
     * @param file 업로드된 파일
     * @param fileType 파일 유형
     * @param unused 사용하지 않는 파라미터 (이전 코드와의 호환성)
     * @return 저장된 FileAttachment 엔티티
     */
    public FileAttachment storeFile(MultipartFile file, String fileType, Object unused) {
        // 파일이 없으면 null 반환
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // 파일명 가져오기
            String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
            
            // 확장자 추출
            String fileExtension = "";
            if (originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
            }
            
            // 현재 날짜 기반 디렉토리 생성
//            String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String directoryPath = uploadDir + "/" + fileType;
            
            // 저장 디렉토리 생성
            Path directory = Paths.get(directoryPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            
            // 고유한 파일명 생성
            String storedFilename = UUID.randomUUID().toString() + "." + fileExtension;
            
            // 파일 저장 경로 생성
            Path targetPath = directory.resolve(storedFilename);
            
            // 파일 저장
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // FileAttachment 엔티티 생성
            FileAttachment fileAttachment = new FileAttachment();
            fileAttachment.setOriginalFilename(originalFilename);
            fileAttachment.setStoredFilename(storedFilename);
            fileAttachment.setContentType(file.getContentType());
            fileAttachment.setFileSize(file.getSize());
            fileAttachment.setFilePath(directoryPath + "/" + storedFilename);
            fileAttachment.setFileExtension(fileExtension);
            fileAttachment.setFileType(fileType);
            
            // DB에 저장
            return fileAttachmentRepository.save(fileAttachment);
            
        } catch (IOException ex) {
            log.error("파일 저장 중 오류 발생: " + ex.getMessage(), ex);
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * 파일 삭제
     * @param fileAttachment 삭제할 파일 엔티티
     * @return 삭제 성공 여부
     */
    public boolean deleteFile(FileAttachment fileAttachment) {
        try {
            // DB에서 삭제
            fileAttachmentRepository.delete(fileAttachment);
            
            // 실제 파일 삭제
            Path filePath = Paths.get(fileAttachment.getFilePath());
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            log.error("파일 삭제 중 오류 발생: " + ex.getMessage(), ex);
            return false;
        }
    }
    
    /**
     * 파일 조회
     * @param fileId 파일 ID
     * @return 파일 엔티티
     */
    public FileAttachment getFile(Long fileId) {
        return fileAttachmentRepository.findById(fileId).orElse(null);
    }
} 