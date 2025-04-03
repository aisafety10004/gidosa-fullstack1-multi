package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileAttachmentJpaRepository extends JpaRepository<FileAttachment, Long> {
    // 특정 파일 유형의 모든 파일 조회
    List<FileAttachment> findByFileType(String fileType);
    
    // 파일명으로 검색
    List<FileAttachment> findByOriginalFilenameContaining(String filename);
} 