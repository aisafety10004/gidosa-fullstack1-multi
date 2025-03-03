package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @OneToOne
    @JoinColumn(name = "file_attachment1_id")
    private FileAttachment fileAttachment1;

    @OneToOne
    @JoinColumn(name = "file_attachment2_id")
    private FileAttachment fileAttachment2;

    @OneToOne
    @JoinColumn(name = "file_attachment3_id")
    private FileAttachment fileAttachment3;

    @OneToOne
    @JoinColumn(name = "file_attachment4_id")
    private FileAttachment fileAttachment4;

    @OneToOne
    @JoinColumn(name = "file_attachment5_id")
    private FileAttachment fileAttachment5;

    @Column(name = "notice_date")
    private LocalDateTime noticeDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    private Construction construction;

    // 첨부파일 목록 조회를 위한 편의 메서드
    @Transient
    public List<FileAttachment> getAttachments() {
        List<FileAttachment> attachments = new ArrayList<>();
        if (fileAttachment1 != null) attachments.add(fileAttachment1);
        if (fileAttachment2 != null) attachments.add(fileAttachment2);
        if (fileAttachment3 != null) attachments.add(fileAttachment3);
        if (fileAttachment4 != null) attachments.add(fileAttachment4);
        if (fileAttachment5 != null) attachments.add(fileAttachment5);
        return attachments;
    }
}
