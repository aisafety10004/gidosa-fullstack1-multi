package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class FileAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;  // 원본 파일명

    @Column(nullable = false)
    private String storedFilename;    // 저장된 파일명

    @Column(nullable = false)
    private String contentType;       // 파일 타입

    @Column(nullable = false)
    private Long fileSize;            // 파일 크기

    @Column(nullable = false)
    private String filePath;          // 파일 저장 경로

    @CreatedDate
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 