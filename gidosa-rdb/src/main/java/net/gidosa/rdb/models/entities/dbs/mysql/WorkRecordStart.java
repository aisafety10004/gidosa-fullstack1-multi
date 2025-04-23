package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkRecordStart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("출근 기록 고유 식별자")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_general_id", nullable = false)
    @Comment("근로자 정보")
    private MemberGeneral memberGeneral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id", nullable = false)
    @Comment("현장 정보")
    private Construction construction;

    @Column(nullable = false)
    @Comment("근로자 이름")
    private String name;

    @Column(nullable = false)
    @Comment("근로자 전화번호")
    private String phone;

    @Column(nullable = false)
    @Comment("건강상태 확인 여부")
    private Boolean healthCheck;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "worker_start_photo_id")
    @Comment("근로자 출근사진")
    private FileAttachment workerStartPhoto;

    @Column
    @Comment("근무 중 외출계획 여부")
    private Boolean planToGoOut;

    @Column
    @Comment("조기 퇴근 예정 여부")
    private Boolean planToLeaveEarly;

    @Column
    @Comment("연장/야간 근무 계획 여부")
    private Boolean planToWorkOvertime;

    @Column(nullable = false)
    @Comment("안전수칙 준수 확인 여부")
    private Boolean safetyRuleCheck;

    @Column(nullable = false)
    @Comment("위험요인 보고 확인 여부")
    private Boolean riskReportCheck;

    @Column(nullable = false)
    @Comment("보호구 착용 확인 여부")
    private Boolean protectiveGearCheck;

    @Column
    @Comment("출근 시간")
    private LocalDateTime checkInTime;

//    @Column
//    @Comment("근무 상태 (출근/퇴근)")
//    private String workStatus;

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
        if (checkInTime == null) {
            checkInTime = LocalDateTime.now();
        }
//        workStatus = "CHECK_IN";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 