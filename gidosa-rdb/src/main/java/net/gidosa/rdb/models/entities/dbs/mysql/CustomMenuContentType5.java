package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomMenuContentType5 {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("타입5 커스텀 메뉴 컨텐츠 고유 식별자")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @Comment("메뉴")
    private CustomMenu menu;
    
    @Column(columnDefinition = "TEXT")
    @Comment("Mermaid 코드")
    private String mermaidCode;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_attachment1_id")
    @Comment("첨부 파일 1")
    private FileAttachment fileAttachment1;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_attachment2_id")
    @Comment("첨부 파일 2")
    private FileAttachment fileAttachment2;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_attachment3_id")
    @Comment("첨부 파일 3")
    private FileAttachment fileAttachment3;

    @CreationTimestamp
    @Comment("생성 일시")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt;
} 