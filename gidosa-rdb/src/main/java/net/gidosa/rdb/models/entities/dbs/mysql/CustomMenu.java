package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomMenu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("커스텀 메뉴 고유 식별자")
    private Long id;

    @Column(nullable = false)
    @Comment("메뉴 이름")
    private String name;

    @Column(nullable = false)
    @Comment("메뉴 URL")
    private String url;

    @Column
    @Comment("메뉴 설명")
    private String description;

    @Column(nullable = false)
    @Comment("메뉴 타입 (1: 단건 내용 저장/보기, 2: 날짜 저장/보기)")
    private Integer menuType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @Comment("상위 메뉴")
    private CustomMenu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("하위 메뉴 목록")
    private List<CustomMenu> children = new ArrayList<>();

    @Column
    @Comment("표시 순서")
    private Integer displayOrder;

    @Column(nullable = false)
    @Comment("활성화 여부")
    private Boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    @Comment("건설 현장")
    private Construction construction;

    @CreationTimestamp
    @Comment("생성 일시")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt;
} 