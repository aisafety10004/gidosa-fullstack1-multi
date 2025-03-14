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
public class WorkDiscussion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("작업 토론 고유 식별자")
    private Long id; // 작업 토론 고유 식별자

    @Column(nullable = false)
    @Comment("토론 제목")
    private String title; // 토론 제목

    @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
    @Comment("토론 내용")
    private String content; // 토론 내용

    @CreatedDate
    @Comment("생성 일시")
    private LocalDateTime createdAt; // 생성 일시

    @LastModifiedDate
    @Comment("수정 일시")
    private LocalDateTime updatedAt; // 수정 일시

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

    // @OneToOne
    // @JoinColumn(name = "file_attachment4_id")
    // @Comment("첨부 파일 4")
    // private FileAttachment fileAttachment4; // 첨부 파일 4

    // @OneToOne
    // @JoinColumn(name = "file_attachment5_id")
    // @Comment("첨부 파일 5")
    // private FileAttachment fileAttachment5; // 첨부 파일 5

    @Column(name = "discussion_date")
    @Comment("토론 일시")
    private LocalDateTime discussionDate; // 토론 일시
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    @Comment("소속 공사 정보")
    private Construction construction; // 소속 공사 정보

    @Column(columnDefinition = "TEXT")
    @Comment("머메이드 코드")
    private String mermaidCode; // 머메이드 코드
    
    @Column(nullable = false)
    @Comment("그룹 이름")
    private String groupName; // 그룹 이름

    // 첨부파일 목록 조회를 위한 편의 메서드
    @Transient
    public List<FileAttachment> getAttachments() {
        List<FileAttachment> attachments = new ArrayList<>();
        if (fileAttachment1 != null) attachments.add(fileAttachment1);
        if (fileAttachment2 != null) attachments.add(fileAttachment2);
        if (fileAttachment3 != null) attachments.add(fileAttachment3);
        // if (fileAttachment4 != null) attachments.add(fileAttachment4);
        // if (fileAttachment5 != null) attachments.add(fileAttachment5);
        return attachments;
    }
} 