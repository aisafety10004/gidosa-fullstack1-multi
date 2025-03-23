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
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("문의사항 고유 식별자")
    private Long id; // 문의사항 고유 식별자

    @Column(nullable = false)
    @Comment("문의사항 제목")
    private String title; // 문의사항 제목

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    @Comment("문의사항 내용")
    private String content; // 문의사항 내용

    @CreatedDate
    @Comment("생성 일시")
    private LocalDateTime createdAt; // 생성 일시

    @LastModifiedDate
    @Comment("수정 일시")
    private LocalDateTime updatedAt; // 수정 일시

    @Column(name = "inquiry_date")
    @Comment("문의 날짜")
    private LocalDateTime inquiryDate; // 문의 날짜
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    @Comment("소속 공사 정보")
    private Construction construction; // 소속 공사 정보

    @OneToOne
    @JoinColumn(name = "file_attachment1_id")
    @Comment("첨부 파일 1")
    private FileAttachment fileAttachment1; // 첨부 파일 1

    @OneToOne
    @JoinColumn(name = "file_attachment2_id")
    @Comment("첨부 파일 2")
    private FileAttachment fileAttachment2; // 첨부 파일 2

    @OneToOne
    @JoinColumn(name = "file_attachment3_id")
    @Comment("첨부 파일 3")
    private FileAttachment fileAttachment3; // 첨부 파일 3

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Comment("답변 여부")
    private Boolean answered = false; // 답변 여부
    
    @Column(columnDefinition = "MEDIUMTEXT")
    @Comment("답변 내용")
    private String answerContent; // 답변 내용
    
    @Column
    @Comment("답변 일시")
    private LocalDateTime answerDate; // 답변 일시
    
    @Column
    @Comment("문의자 이름")
    private String inquirerName; // 문의자 이름
    
    @Column
    @Comment("문의자 이메일")
    private String inquirerEmail; // 문의자 이메일
    
    @Column
    @Comment("문의자 연락처")
    private String inquirerPhone; // 문의자 연락처
    
    @Column(columnDefinition = "VARCHAR(50)")
    @Comment("문의 유형")
    private String inquiryType; // 문의 유형
    
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    @Comment("비공개 여부")
    private Boolean isPrivate = false; // 비공개 여부

    // 첨부파일 목록 조회를 위한 편의 메서드
    @Transient
    public List<FileAttachment> getAttachments() {
        List<FileAttachment> attachments = new ArrayList<>();
        if (fileAttachment1 != null) attachments.add(fileAttachment1);
        if (fileAttachment2 != null) attachments.add(fileAttachment2);
        if (fileAttachment3 != null) attachments.add(fileAttachment3);
        return attachments;
    }
} 