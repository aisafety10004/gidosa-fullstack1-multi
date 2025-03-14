package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("관리자 회원 고유 식별자")
    private Long id; // 관리자 회원 고유 식별자

    @Column(nullable = false, unique = true)
    @Comment("관리자 아이디")
    private String username; // 관리자 아이디

    @Column(nullable = false)
    @Comment("관리자 비밀번호")
    private String password; // 관리자 비밀번호

    @Column(nullable = false)
    @Comment("관리자 이름")
    private String name; // 관리자 이름

    @Column(nullable = false)
    @Comment("관리자 위치")
    private String location; // 관리자 위치

    @Column
    @Comment("관리자 상태")
    private String status; // 관리자 상태

    @Column(nullable = false)
    @Comment("관리자 권한")
    private String role = "ROLE_MANAGER"; // 관리자 권한

    @Column
    @Comment("관리자 부서")
    private String department; // 관리자 부서

    @Column
    @Comment("관리자 직책")
    private String position; // 관리자 직책

    @Column(nullable = false)
    @Comment("관리자 이메일")
    private String email; // 관리자 이메일

    @Column(nullable = false)
    @Comment("관리자 전화번호")
    private String phone; // 관리자 전화번호

    @Column
    @Comment("관리자 활성화 여부")
    private boolean isActive; // 관리자 활성화 여부

    @CreationTimestamp
    @Comment("생성 일시")
    private LocalDateTime createdAt; // 생성 일시

    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt; // 수정 일시

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constructionId", referencedColumnName = "id")
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
