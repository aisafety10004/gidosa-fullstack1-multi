package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Construction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("공사 고유 식별자")
    private Long id; // 공사 고유 식별자

    @Column(nullable = false)
    @Comment("공사명")
    private String name; // 공사명

    @Column(nullable = false)
    @Comment("공사 위치")
    private String location; // 공사 위치

    @Column
    @Comment("공사 시작일")
    private LocalDateTime startDate; // 공사 시작일

    @Column
    @Comment("공사 종료일")
    private LocalDateTime endDate; // 공사 종료일

    @Column
    @Comment("공사 상태")
    private String status; // 공사 상태

    @Column
    @Comment("공사 설명")
    private String description; // 공사 설명

    @Column
    @Comment("생성 일시")
    private LocalDateTime createdAt; // 생성 일시

    @Column
    @Comment("수정 일시")
    private LocalDateTime updatedAt; // 수정 일시

    @OneToMany(mappedBy = "construction")
    @Comment("일반 회원 목록")
    private List<MemberGeneral> memberGeneralList; // 일반 회원 목록

    @OneToMany(mappedBy = "construction")
    @Comment("관리자 회원 목록")
    private List<MemberAdmin> memberAdminList; // 관리자 회원 목록

    @ElementCollection
    @Comment("관리 메뉴 목록")
    private List<String> managementMenus; // 관리 메뉴 목록

    // @ElementCollection
    // @Comment("선택된 커스텀 메뉴 ID 목록")
    @Transient // DB에 저장되지 않는 필드
    private List<Long> selectedCustomMenuIds; // 선택된 커스텀 메뉴 ID 목록
    
    @Transient // DB에 저장되지 않는 필드
    private List<CustomMenu> customMenus; // 실제 커스텀 메뉴 객체 목록 (조회용)

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getStartDateStr() {
        if (startDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return startDate.format(formatter);
    }

    public String getEndDateStr() {
        if (endDate == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return endDate.format(formatter);
    }
}
