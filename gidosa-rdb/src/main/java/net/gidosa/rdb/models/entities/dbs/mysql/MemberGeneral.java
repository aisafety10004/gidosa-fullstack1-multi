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
public class MemberGeneral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("일반 회원 고유 식별자")
    private Long id; // 일반 회원 고유 식별자

    @Column(nullable = false, unique = true)
    @Comment("회원 아이디")
    private String username; // 회원 아이디

    @Column(nullable = false)
    @Comment("회원 비밀번호")
    private String password; // 회원 비밀번호

    @Column(nullable = false)
    @Comment("회원 이름")
    private String name; // 회원 이름

    @Column(nullable = false, unique = true)
    @Comment("회원 이메일")
    private String email; // 회원 이메일

    @Column(nullable = false)
    @Comment("회원 전화번호")
    private String phone; // 회원 전화번호

    @Column(nullable = false)
    @Comment("회원 권한")
    private String role = "ROLE_USER"; // 회원 권한

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "constructionId", referencedColumnName = "id", updatable = false)
    @Comment("소속 공사 정보")
    private Construction construction; // 소속 공사 정보

    @CreationTimestamp
    @Comment("생성 일시")
    private LocalDateTime createdAt; // 생성 일시

    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt; // 수정 일시
}
