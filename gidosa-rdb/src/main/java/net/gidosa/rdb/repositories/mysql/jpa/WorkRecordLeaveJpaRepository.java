package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import net.gidosa.rdb.models.entities.dbs.mysql.WorkRecordLeave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRecordLeaveJpaRepository extends JpaRepository<WorkRecordLeave, Long> {
    
    // 특정 근로자의 퇴근 기록 조회
    List<WorkRecordLeave> findByMemberGeneralOrderByCreatedAtDesc(MemberGeneral memberGeneral);
    
    // 특정 현장의 퇴근 기록 조회
    List<WorkRecordLeave> findByConstructionOrderByCreatedAtDesc(Construction construction);
    
    // 특정 날짜에 대한 퇴근 기록 조회
    List<WorkRecordLeave> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    // 가장 최근 퇴근 기록 조회
    Optional<WorkRecordLeave> findTopByMemberGeneralOrderByCreatedAtDesc(MemberGeneral memberGeneral);
    
    // 특정 근로자의 특정 날짜 범위 내 퇴근 기록 조회
    WorkRecordLeave findByMemberGeneralAndCreatedAtBetween(MemberGeneral memberGeneral, LocalDateTime start, LocalDateTime end);
} 