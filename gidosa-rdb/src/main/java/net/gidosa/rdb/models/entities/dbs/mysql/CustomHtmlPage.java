package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CustomHtmlPage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("HTML 페이지 고유 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("HTML 페이지 제목")
    private String title;
    
    @Column(columnDefinition = "MEDIUMTEXT")
    @Comment("HTML 페이지 상세내용")
    private String content;
    
    @OneToOne
    @JoinColumn(name = "html_file_id")
    @Comment("HTML 파일 첨부")
    private FileAttachment htmlFile;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Comment("게시 여부")
    private Boolean published = false;
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Comment("메인 페이지 여부")
    private Boolean isMainPage = false;
    
    @CreatedDate
    @Comment("생성 일시")
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    @Comment("소속 공사 정보")
    private Construction construction; // 소속 공사 정보
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 