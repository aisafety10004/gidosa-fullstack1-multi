package net.gidosa.rdb.models.entities.dbs.mysql;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskFactor {
    // --- 신규등록 관련
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDate executionDate;                    // 실시일자(후 입력)

    @Enumerated(EnumType.STRING)
    @Column
    private RiskFactorEvaluationType riskFactorEvaluationType;     // 평가구분(후 입력)

    @OneToOne
    private MasterAuthorityDivision authorityDivision;          // 관할구분(후 입력)

    @Enumerated(EnumType.STRING)
    @Column
    private RiskFactorAuthorityDivisionLevel authorityDivisionLevel;     // 관할급(후 입력)

    @OneToOne
    private MasterDivisionDetail divisionDetail;                // 기관명(후 입력)

    ////////////////////////////////////////////////////////////////
    // ----------------------------------------------- 신규등록 관련
    @Column(nullable = false)
    private String siteName;             // 현장명

    @Column(nullable = false)
    private String workProcess;          // 작업공정

    @Column
    private String workLocation;          // 작업위치(위치층수)

    @Column(nullable = false)
    private String workImage1Url;          // 현장사진1

    @Column
    private String workImage2Url;          // 현장사진2

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskClassification riskClassification;      // 위험 분류

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskDetailFactor riskDetailFactor;          // 위험 요인(상세)

    @Column(nullable = false)
    private String riskSituationResult;    // (예상)위험상황 및 결과

    @Column
    private String currentSafetyMeasure;    // 현재안전조치

    @Column(nullable = false)
    private byte riskPossibility;    // 가능성

    @Column(nullable = false)
    private byte riskCriticality;    // 중대성

//    @Column(nullable = false)
//    private short riskSize;   // 위험성크기(가능성 * 중대성)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskReductionMeasureFirst riskReductionMeasure1;   // 위험성 감소대책1(선택박스)

    @Column(nullable = false)
    private String riskReductionMeasure2;   // 위험성 감소대책2(수기)

    @Enumerated(EnumType.STRING)
    @Column
    private RiskMeasureCompletion isRiskMeasureCompletion;   // 조치여부

    ////////////////////////////////////////////////////////////////
    @Column
    private Double latitude;               // 현장 위도값(후 입력)
    
    @Column
    private Double longitude;              // 현장 경도값(후 입력)

    @Column
    private String relatedLaw;              // 관련법령(후 입력)

    @Column
    private Boolean isRiskReductionMeasure;   // 위험성감소 대책수립 여부(후 입력)

    @Column
    private String evaluator1;   // 평가자1(후 입력)

    @Column
    private String evaluator2;   // 평가자2(후 입력)

    ////////////////////////////////////////////////////////////////
    // -----------------------------------------------  개선등록 관련
    @Column
    private String impResult;   // 개선결과

    @Column
    private byte impRiskPossibility;    // 개선 가능성

    @Column
    private byte impRiskCriticality;    // 개선 중대성

    // @Column(nullable = false)
    // private short impRiskSize;          // 개선 위험성크기(가능성 * 중대성)

    

    // ------------------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "construction_id", nullable = false)
    private Construction construction;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // ------------------------------------------------------------------------

    public enum RiskFactorEvaluationType {
        REGULAR("정기"),
        OCCASIONAL("수시"),
        INITIAL("최초"),
        ;
        private final String displayName;

        RiskFactorEvaluationType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }   
    }

    public enum RiskFactorAuthorityDivisionLevel {
        LEVEL1("지원청"),
        LEVEL2("유"),
        LEVEL3("초"),
        LEVEL4("중"),
        LEVEL5("고"),
        LEVEL6("대"),
        LEVEL_OTHER("기타"),
        ;

        private final String displayName;

        RiskFactorAuthorityDivisionLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RiskClassification {
        MECHANICAL_EQUIPMENT("기계(설비)적요인"),
        ELECTRICAL("전기(설비)적요인"),
        CHEMICAL_SUBSTANCE("화학(물질)적요인"),
        BIOLOGICAL("생물학적요인"),
        WORK_CHARACTERISTICS("작업특성상요인"),
        WORK_ENVIRONMENT("작업환경요인"),
        ;

        private final String displayName;

        RiskClassification(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RiskDetailFactor {
        FALLING("떨어짐"),
        SLIPPING("넘어짐"),
        COLLISION_CONTACT("부딪힘/접촉"),
        CAUGHT_IN_ENTANGLEMENT("끼임"),
        COLLAPSE("무너짐"),
        CUTTING_LACERATION_PUNCTURE("절단/베임/찔림"),
        STRUCK_BY_OBJECT("맞음"),
        ELECTRIC_SHOCK("감전"),
        FIRE_EXPLOSION("화재/폭발"),
        MUSCULOSKELETAL_DISORDER("근골질환"),
        EXPOSURE_TO_EXTREME_TEMPERATURES("이상온도 노출, 접촉"),
        OTHER("기타"),
        ;

        private final String displayName;

        RiskDetailFactor(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RiskReductionMeasureFirst {
        EXAMPLE1("1. 위험한 작업의 폐지·변경, 유해·위험물질 대체 등의 조치, 설계나 계획 단계에서 위험성을 제거 또는 저감하는 조치"),
        EXAMPLE2("2. 연동장치, 환기장치 설치 등의 공학적 대책"),
        EXAMPLE3("3. 작업장 작업절차서 정비 등의 관리적 대책"),
        EXAMPLE4("4. 개인용 보호구의 사용 "),
        ;

        private final String displayName;

        RiskReductionMeasureFirst(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RiskMeasureCompletion {
        COMPLETED("조치완료"),
        PLANNING("조치예정"),
        ;

        private final String displayName;

        RiskMeasureCompletion(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
} 