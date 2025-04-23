package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkRecordLeave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("퇴근 기록 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_general_id")
    @Comment("근로자 정보")
    private MemberGeneral memberGeneral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id")
    @Comment("현장 정보")
    private Construction construction;

    @Column(length = 50, nullable = false)
    @Comment("근로자 이름")
    private String name;

    @Column(length = 20, nullable = false)
    @Comment("근로자 전화번호")
    private String phone;

    // 1. 업무수행결과 점검
    @Column(name = "best_effort_check")
    @Comment("작업중 최선을 다해 업무를 수행 여부")
    private Boolean bestEffortCheck;

    @Column(name = "no_accident_check")
    @Comment("작업중 무재해로서 퇴근함을 확인 여부")
    private Boolean noAccidentCheck;

    // 2. 퇴근 근로자 사진
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "worker_leave_photo_id")
    @Comment("근로자 퇴근사진")
    private FileAttachment workerLeavePhoto;

    // 3. 근무중 개선사항
    @Column(name = "improvement_suggestions", length = 1000)
    @Comment("근무중 개선사항 내용")
    private String improvementSuggestions;

    // 4. 출근 계획
    @Column(name = "plan_to_come_tomorrow", nullable = false)
    @Comment("내일 정상출근 여부")
    private Boolean planToComeNextDay;

//    @CreatedDate
    @CreationTimestamp
    @Column(updatable = false)
    @Comment("생성 일시")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Comment("수정 일시")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
//        if (checkOutTime == null) {
//            checkOutTime = LocalDateTime.now();
//        }
//        workStatus = "CHECK_OUT";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 