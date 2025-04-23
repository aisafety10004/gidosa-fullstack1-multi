package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.*;
import net.gidosa.rdb.repositories.mysql.jpa.FileAttachmentJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.WorkRecordStartJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.WorkRecordLeaveJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralWorkRecordService {
    private final WorkRecordStartJpaRepository workRecordStartJpaRepository;
    private final WorkRecordLeaveJpaRepository workRecordLeaveJpaRepository;
    private final FileAttachmentJpaRepository fileAttachmentJpaRepository;
    private final FileStorageService fileStorageService;

    /**
     * 근로자 출근 기록 저장
     */
    @Transactional
    public WorkRecordStart saveWorkRecordStart(MemberGeneral memberGeneral, Construction construction,
                                               String name, String phone, Boolean healthCheck,
                                               MultipartFile workerStartPhotoFile,
                                               Boolean planToGoOut, Boolean planToLeaveEarly, Boolean planToWorkOvertime,
                                               Boolean safetyRuleCheck, Boolean riskReportCheck, Boolean protectiveGearCheck) {
        
        // 필수 체크사항 검증
        if (!healthCheck || !safetyRuleCheck || !riskReportCheck || !protectiveGearCheck) {
            throw new IllegalArgumentException("필수 체크사항을 모두 체크해주세요.");
        }
        
        // 파일 저장 처리
        FileAttachment workerStartPhoto = null;
        if (workerStartPhotoFile != null && !workerStartPhotoFile.isEmpty()) {
            workerStartPhoto = fileStorageService.storeFile(workerStartPhotoFile, "worker_start_photo", null);
            fileAttachmentJpaRepository.save(workerStartPhoto);
        } else {
            throw new IllegalArgumentException("근로자 출근사진을 첨부해주세요.");
        }
        
        // WorkRecord 엔티티 생성 및 저장
        WorkRecordStart workRecordStart = WorkRecordStart.builder()
                .memberGeneral(memberGeneral)
                .construction(construction)
                .name(name)
                .phone(phone)
                .healthCheck(healthCheck)
                .workerStartPhoto(workerStartPhoto)
                .planToGoOut(planToGoOut)
                .planToLeaveEarly(planToLeaveEarly)
                .planToWorkOvertime(planToWorkOvertime)
                .safetyRuleCheck(safetyRuleCheck)
                .riskReportCheck(riskReportCheck)
                .protectiveGearCheck(protectiveGearCheck)
                .checkInTime(LocalDateTime.now())
//                .workStatus("CHECK_IN")
                .build();
        
        return workRecordStartJpaRepository.save(workRecordStart);
    }
    
    /**
     * 근로자의 가장 최근 출근 기록 조회
     */
    public Optional<WorkRecordStart> getLatestWorkRecord(Long memberGeneralId) {
        return workRecordStartJpaRepository.findTopByMemberGeneralIdOrderByCreatedAtDesc(memberGeneralId);
    }
    
    /**
     * 근로자의 모든 출근 기록 조회
     */
    public List<WorkRecordStart> getAllWorkRecordsByMemberGeneral(Long memberGeneralId) {
        return workRecordStartJpaRepository.findByMemberGeneralIdOrderByCreatedAtDesc(memberGeneralId);
    }
    
    /**
     * 현장의 모든 출근 기록 조회
     */
    public List<WorkRecordStart> getAllWorkRecordsByConstruction(Long constructionId) {
        return workRecordStartJpaRepository.findByConstructionIdOrderByCreatedAtDesc(constructionId);
    }

    /**
     * 근로자의 오늘 퇴근 기록 조회
     */
    @Transactional(readOnly = true)
    public WorkRecordLeave getTodayWorkRecordLeave(MemberGeneral memberGeneral) {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        return workRecordLeaveJpaRepository.findByMemberGeneralAndCreatedAtBetween(
                memberGeneral, startOfDay, endOfDay);
    }

    /**
     * 퇴근 기록 저장
     */
    @Transactional
    public WorkRecordLeave saveWorkRecordLeave(
            MemberGeneral memberGeneral,
            Construction construction,
            String name,
            String phone,
            Boolean bestEffortCheck,
            Boolean noAccidentCheck,
            MultipartFile workerLeavePhotoFile,
            String improvementSuggestions,
            Boolean planToComeNextDay
    ) {
        try {
            // 파일 저장 처리
            FileAttachment workerLeavePhoto = null;
            if (workerLeavePhotoFile != null && !workerLeavePhotoFile.isEmpty()) {
                workerLeavePhoto = fileStorageService.storeFile(workerLeavePhotoFile, "worker_leave_photo", null);
                fileAttachmentJpaRepository.save(workerLeavePhoto);
            } else {
                throw new IllegalArgumentException("근로자 퇴근사진을 첨부해주세요.");
            }


            // 퇴근 기록 생성 및 저장
            WorkRecordLeave workRecordLeave = WorkRecordLeave.builder()
                    .memberGeneral(memberGeneral)
                    .construction(construction)
                    .name(name)
                    .phone(phone)
                    .bestEffortCheck(bestEffortCheck)
                    .noAccidentCheck(noAccidentCheck)
                    .workerLeavePhoto(workerLeavePhoto)
                    .improvementSuggestions(improvementSuggestions)
                    .planToComeNextDay(planToComeNextDay)
                    .build();

            return workRecordLeaveJpaRepository.save(workRecordLeave);
        } catch (Exception e) {
            log.error("퇴근 기록 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("퇴근 기록 저장 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}