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
    
    @Value("${file.upload.path:uploads/files}")
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
}