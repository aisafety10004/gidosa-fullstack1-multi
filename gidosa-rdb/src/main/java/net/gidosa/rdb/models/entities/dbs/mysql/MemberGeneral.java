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

    @Column
    @Comment("회원 직책")
    private String position; // 회원 직책
    
    @Column
    @Comment("회원 직종")
    private String jobType; // 회원 직종
    
    @Column
    @Comment("비상연락번호")
    private String emergencyContact; // 비상연락번호
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_photo_id")
    @Comment("프로필 사진")
    private FileAttachment profilePhoto; // 프로필 사진
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "labor_contract_id")
    @Comment("근로계약서")
    private FileAttachment laborContract; // 근로계약서
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "safety_education_cert_id")
    @Comment("건설업기초안전보건교육이수증")
    private FileAttachment safetyEducationCert; // 건설업기초안전보건교육이수증
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "protective_gear_pledge_id")
    @Comment("보호구착용서약서")
    private FileAttachment protectiveGearPledge; // 보호구착용서약서
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "etc_doc1_id")
    @Comment("기타문서1")
    private FileAttachment etcDoc1; // 기타문서1
    
    @Column
    @Comment("기타문서1 설명")
    private String etcDoc1Description; // 기타문서1 설명
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "etc_doc2_id")
    @Comment("기타문서2")
    private FileAttachment etcDoc2; // 기타문서2
    
    @Column
    @Comment("기타문서2 설명")
    private String etcDoc2Description; // 기타문서2 설명
    
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "etc_doc3_id")
    @Comment("기타문서3")
    private FileAttachment etcDoc3; // 기타문서3
    
    @Column
    @Comment("기타문서3 설명")
    private String etcDoc3Description; // 기타문서3 설명

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
