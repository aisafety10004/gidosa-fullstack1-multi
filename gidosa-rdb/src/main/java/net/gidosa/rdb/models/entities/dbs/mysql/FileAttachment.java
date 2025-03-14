package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FileAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("파일 첨부 고유 식별자")
    private Long id; // 파일 첨부 고유 식별자

    @Column(nullable = false)
    @Comment("원본 파일명")
    private String originalFilename; // 원본 파일명

    @Column(nullable = false)
    @Comment("저장 파일명")
    private String storedFilename; // 저장 파일명

    @Column(nullable = false)
    @Comment("파일 타입")
    private String contentType; // 파일 타입

    @Column(nullable = false)
    @Comment("파일 크기")
    private Long fileSize; // 파일 크기

    @Column(nullable = false)
    @Comment("파일 저장 경로")
    private String filePath; // 파일 저장 경로

    @Column(nullable = false)
    @Comment("파일 확장자")
    private String fileExtension; // 파일 확장자

    @CreatedDate
    @Comment("생성 일시")
    private LocalDateTime createdAt; // 생성 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    @Comment("소속 공사 정보")
    private Construction construction; // 소속 공사 정보

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 