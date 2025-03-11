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

    // @Enumerated(EnumType.STRING)
    // @Column(nullable = false)
    // private RiskDetailFactor riskDetailFactor;          // 위험 요인(상세)
    @Column(nullable = false)
    private String riskDetailFactor;          // 위험 요인(상세)

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

    // @Column
    // private short impRiskSize;          // 개선 위험성크기(가능성 * 중대성)

    @Column
    private String impWorkImage1Url;       // 개선 현장사진1

    @Column
    private String impWorkImage2Url;          // 개선 현장사진2

    @Column
    private Short impCountCorrectAction;      // 개선조치 이행건수

    @Column
    private Short impCountNoCorrectAction;    // 미개선 조치건수

    @Column
    private String impNoCorrectContent;        // 미개선 조치내용

    @Column
    private String impNoCorrectContentPlan;    // 미개선사항 조치계획

    @Column
    private LocalDate impCorrectDate;      // 개선조치일

    @Column
    private String impCorrectPerson;    // 개선조치 담당자

    @Column
    private LocalDate impCorrectCompletionDate;      // 개선조치 완료일

    @Column
    private String impCorrectConfirmPerson;      // 개선조치 확인자

    @OneToOne
    @JoinColumn(name = "file_attachment1_id")
    private FileAttachment fileAttachment1;

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

    // 기계(설비)적 요인
    public enum RiskDetailFactorMechanicalEquipment {
        MECHANICAL_EQUIPMENT_STUCKNESS("끼임(감김)"),
        MECHANICAL_EQUIPMENT_SURFACE("위험한 표면(절단, 베임 등)"),
        MECHANICAL_EQUIPMENT_DROPPING_UPDOWN("기계(설비)의 낙하, 비래, 전복, 전도"),
        MECHANICAL_EQUIPMENT_COLLISION("충돌위험"),
        MECHANICAL_EQUIPMENT_COLLAPSE("넘어짐(미끄러짐, 걸림 등)"),
        MECHANICAL_EQUIPMENT_FALLING_RISK("추락위험(개구부)"),
        ;

        private final String displayName;

        RiskDetailFactorMechanicalEquipment(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    // 전기적 요인
    public enum RiskDetailFactorElectrical {
        ELECTRICAL_SHOCK("감전(안전전압초과)"),
        ELECTRICAL_SURFACE("아크"),
        ELECTRICAL_STATIC("정전기"),
        ELECTRICAL_EXPLOSION("화재/폭발 위험"),
        ;

        private final String displayName;

        RiskDetailFactorElectrical(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    // 화학(물질)적 요인
    public enum RiskDetailFactorChemicalSubstance {
        CHEMICAL_SUBSTANCE_GAS("가스"),
        CHEMICAL_SUBSTANCE_VAPOR("증기"),
        CHEMICAL_SUBSTANCE_AEROSOL("에어로졸-흠"),
        CHEMICAL_SUBSTANCE_LIQUID_MIST("액체, 미스트"),
        CHEMICAL_SUBSTANCE_SOLID("고체(분진)"),
        CHEMICAL_SUBSTANCE_REACTIVE_SUBSTANCE("반응성 물질"),
        CHEMICAL_SUBSTANCE_RADIATION("방사선"),
        CHEMICAL_SUBSTANCE_EXPLOSIVES_DETONATION("화재/폭발"),
        CHEMICAL_SUBSTANCE_COMBUSTION_EXPLOSION("복사열/폭발과압"),
        ;

        private final String displayName;

        RiskDetailFactorChemicalSubstance(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    // 생물학적 요인
    public enum RiskDetailFactorBiological {
        BIOLOGICAL_PATHOGEN("병원성 미생물, 바이러스 감염"),
        BIOLOGICAL_GENETICALLY_ORGANISMS("유전자 변형물질(GMO)"),
        BIOLOGICAL_ALLERGENS_MICROORGANISMS("알러지 및 미생물"),
        BIOLOGICAL_ANIMALS("동물"),
        BIOLOGICAL_PLANTS("식물"),
        ;

        private final String displayName;

        RiskDetailFactorBiological(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    // 작업특성 요인
    public enum RiskDetailFactorWorkCharacteristics {
        WORK_CHARACTERISTICS_NOISE("소음"),
        WORK_CHARACTERISTICS_SHOCKWAVE("초음파-초저주파음"),
        WORK_CHARACTERISTICS_VIBRATION("진동"),
        WORK_CHARACTERISTICS_WORKER_MISTAKE("근로자의 실수(휴먼에러)"),
        WORK_CHARACTERISTICS_LOW_HIGH_TEMPERATURE ("저압 또는 고압상태"),
        WORK_CHARACTERISTICS_THERMAL_HAZARD("질식위험-산소결핍"),
        WORK_CHARACTERISTICS_HEAVY_LIFTING("중량물 취급작업"),
        WORK_CHARACTERISTICS_REPETITIVE_TASKS("반복작업"),
        WORK_CHARACTERISTICS_UNSTABLE_WORKING("불안정한 작업자세"),
        WORK_CHARACTERISTICS_WORK_TOOLS("작업(조작)도구"),
        WORK_CHARACTERISTICS_CLIMATE_ALTITUDE_COLD("기후/고온/한랭"),
        ;

        private final String displayName;

        RiskDetailFactorWorkCharacteristics(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
    // 작업환경 요인
    public enum RiskDetailFactorWorkEnvironment {
        WORK_ENVIRONMENT_CLIMATE_ALTITUDE_COLD("기후/고온/한랭"),
        WORK_ENVIRONMENT_LIGHTING("조명"),
        WORK_ENVIRONMENT_SPACE_MOVEMENT("공간 및 이동통로"),
        WORK_ENVIRONMENT_WORK_POSTURE("주변 근로자"),
        WORK_ENVIRONMENT_WORKING_HOURS("작업시간"),
        WORK_ENVIRONMENT_ORGANIZATIONAL_SAFETY_CULTURE("조직 안전문화"),
        WORK_ENVIRONMENT_CHEMICAL_BURNS("화상"),
        WORK_ENVIRONMENT_WORK_TOOLS("작업(조작)도구"),
        ;

        private final String displayName;

        RiskDetailFactorWorkEnvironment(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

//    public enum RiskDetailFactorTest {
//        FALLING("떨어짐"),
//        SLIPPING("넘어짐"),
//        COLLISION_CONTACT("부딪힘/접촉"),
//        CAUGHT_IN_ENTANGLEMENT("끼임"),
//        COLLAPSE("무너짐"),
//        CUTTING_LACERATION_PUNCTURE("절단/베임/찔림"),
//        STRUCK_BY_OBJECT("맞음"),
//        ELECTRIC_SHOCK("감전"),
//        FIRE_EXPLOSION("화재/폭발"),
//        MUSCULOSKELETAL_DISORDER("근골질환"),
//        EXPOSURE_TO_EXTREME_TEMPERATURES("이상온도 노출, 접촉"),
//        OTHER("기타"),
//        ;
//
//        private final String displayName;
//
//        RiskDetailFactorTest(String displayName) {
//            this.displayName = displayName;
//        }
//
//        public String getDisplayName() {
//            return displayName;
//        }
//    }

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

    /**
     * 위험 분류에 따른 위험 요인(상세)의 displayName을 반환합니다.
     * @return 위험 요인(상세)의 displayName
     */
    public String getRiskDetailFactorDisplayName() {
        if (riskDetailFactor == null || riskClassification == null) {
            return riskDetailFactor;
        }

        try {
            switch (riskClassification) {
                case MECHANICAL_EQUIPMENT:
                    return RiskDetailFactorMechanicalEquipment.valueOf(riskDetailFactor).getDisplayName();
                case ELECTRICAL:
                    return RiskDetailFactorElectrical.valueOf(riskDetailFactor).getDisplayName();
                case CHEMICAL_SUBSTANCE:
                    return RiskDetailFactorChemicalSubstance.valueOf(riskDetailFactor).getDisplayName();
                case BIOLOGICAL:
                    return RiskDetailFactorBiological.valueOf(riskDetailFactor).getDisplayName();
                case WORK_CHARACTERISTICS:
                    return RiskDetailFactorWorkCharacteristics.valueOf(riskDetailFactor).getDisplayName();
                case WORK_ENVIRONMENT:
                    return RiskDetailFactorWorkEnvironment.valueOf(riskDetailFactor).getDisplayName();
                default:
                    return riskDetailFactor;
            }
        } catch (IllegalArgumentException e) {
            // 해당 Enum에 일치하는 값이 없는 경우 원래 문자열 반환
            return riskDetailFactor;
        }
    }
} 