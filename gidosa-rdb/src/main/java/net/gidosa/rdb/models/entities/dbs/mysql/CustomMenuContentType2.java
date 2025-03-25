package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomMenuContentType2 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("타입2 커스텀 메뉴 컨텐츠 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id", nullable = false)
    @Comment("메뉴")
    private CustomMenu menu;

    @Column(columnDefinition = "MEDIUMTEXT")
    @Comment("컨텐츠 내용")
    private String content;

    @Column(nullable = false)
    @Comment("컨텐츠 날짜")
    private LocalDate contentDate;

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