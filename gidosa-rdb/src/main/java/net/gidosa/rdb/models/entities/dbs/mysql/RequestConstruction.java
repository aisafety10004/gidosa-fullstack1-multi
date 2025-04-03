package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class RequestConstruction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("공사 요청 고유 식별자")
    private Long id; // 공사 요청 고유 식별자

    @Column(nullable = false)
    @Comment("요청자 이름")
    private String name; // 요청자 이름
    
    @Column(nullable = false)
    @Comment("요청자 전화번호")
    private String phone; // 요청자 전화번호
    
    @Column
    @Comment("공사 위치")
    private String constructionLocation; // 공사 위치
    
    @Column
    @Comment("요청 상태")
    private String position; // 요청 상태
    
    @Column(nullable = false, length = 2000)
    @Comment("문의 메시지")
    private String message; // 문의 메시지
    
    @Column(nullable = false)
    @Comment("동의 여부")
    private boolean agreement; // 동의 여부
    
    @CreatedDate
    @Comment("생성 일시")
    @CreationTimestamp
    private LocalDateTime createdAt; // 생성 일시
    
    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt; // 수정 일시
    
    // OneToOne 관계로 변경
    @OneToOne
    @JoinColumn(name = "business_card_id")
    @Comment("명함 첨부 파일")
    private FileAttachment businessCard; // 명함 첨부 파일
    
    @OneToOne
    @JoinColumn(name = "business_license_id")
    @Comment("사업자등록증 첨부 파일")
    private FileAttachment businessLicense; // 사업자등록증 첨부 파일
    
    @OneToOne
    @JoinColumn(name = "insurance_certificate_id")
    @Comment("고용산재보험가입증명원 첨부 파일")
    private FileAttachment insuranceCertificate; // 고용산재보험가입증명원 첨부 파일
    
    // 첨부파일 목록 조회를 위한 편의 메서드
    @Transient
    public List<FileAttachment> getAttachments() {
        List<FileAttachment> attachments = new ArrayList<>();
        if (businessCard != null) attachments.add(businessCard);
        if (businessLicense != null) attachments.add(businessLicense);
        if (insuranceCertificate != null) attachments.add(insuranceCertificate);
        return attachments;
    }
}
