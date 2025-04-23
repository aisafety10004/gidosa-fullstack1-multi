package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.WorkRecordStart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkRecordStartJpaRepository extends JpaRepository<WorkRecordStart, Long> {
    
    // 근로자 ID로 해당 근로자의 출근 기록 조회 (가장 최신순)
    List<WorkRecordStart> findByMemberGeneralIdOrderByCreatedAtDesc(Long memberGeneralId);
    
    // 근로자 ID와 근무 상태로 출근 기록 조회
//    List<WorkRecordStart> findByMemberGeneralIdAndWorkStatusOrderByCreatedAtDesc(Long memberGeneralId, String workStatus);
    
    // 근로자의 가장 최근 근무 기록 조회
    Optional<WorkRecordStart> findTopByMemberGeneralIdOrderByCreatedAtDesc(Long memberGeneralId);
    
    // 현장 ID로 출근 기록 조회 (가장 최신순)
    List<WorkRecordStart> findByConstructionIdOrderByCreatedAtDesc(Long constructionId);
    
    // 특정 날짜 범위 내의 출근 기록 조회
    @Query("SELECT wr FROM WorkRecordStart wr WHERE wr.checkInTime BETWEEN :startDate AND :endDate ORDER BY wr.checkInTime DESC")
    List<WorkRecordStart> findByCheckInTimeBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // 특정 날짜에 출근한 근로자 수 조회
    @Query("SELECT COUNT(DISTINCT wr.memberGeneral.id) FROM WorkRecordStart wr WHERE DATE(wr.checkInTime) = DATE(:date)")
    Long countDistinctMemberGeneralByDate(@Param("date") LocalDateTime date);
    
    // 특정 현장과 날짜 범위 내의 출근 기록 조회
    @Query("SELECT wr FROM WorkRecordStart wr WHERE wr.construction.id = :constructionId AND wr.checkInTime BETWEEN :startDate AND :endDate ORDER BY wr.checkInTime DESC")
    List<WorkRecordStart> findByConstructionIdAndCheckInTimeBetween(
            @Param("constructionId") Long constructionId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
} 