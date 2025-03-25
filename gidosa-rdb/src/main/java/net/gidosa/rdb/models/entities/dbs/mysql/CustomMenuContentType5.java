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

    // @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    // @Comment("매니저 공개 여부")
    // private Boolean publishedManager = false; // 공개 여부

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Comment("익명 사용자 공개 여부(로그인하지 않은 사용자)")
    private Boolean publishedAnonymous = false; // 익명 사용자 공개 여부
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Comment("로그인 사용자 공개 여부")
    private Boolean publishedLoggedInUser = false; // 로그인 사용자 공개 여부

    @CreationTimestamp
    @Comment("생성 일시")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt;
} 