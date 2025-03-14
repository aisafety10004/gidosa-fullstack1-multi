package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.WorkDiscussion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WorkDiscussionJpaRepository extends JpaRepository<WorkDiscussion, Long> {
    
    @Query("SELECT w FROM WorkDiscussion w LEFT JOIN FETCH w.construction WHERE w.id = :id")
    Optional<WorkDiscussion> findByIdWithConstructionAndAttachments(@Param("id") Long id);
    
    // 특정 건설현장 ID에 해당하는 업무협의 또는 모든 업무협의(construction_id가 null인 경우) 조회
    @Query("SELECT w FROM WorkDiscussion w LEFT JOIN FETCH w.construction c WHERE w.construction.id = :constructionId OR w.construction IS NULL ORDER BY w.id DESC")
    Page<WorkDiscussion> findByConstructionIdOrConstructionIsNullWithConstructionOrderByIdDesc(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // 모든 사용자에게 보이는 업무협의만 조회 (construction_id가 null인 경우)
    Page<WorkDiscussion> findByConstructionIsNullOrderByIdDesc(Pageable pageable);
    
    // 그룹별 업무협의 조회
    Page<WorkDiscussion> findByGroupName(String groupName, Pageable pageable);
    
    // 건설현장 ID와 그룹별 업무협의 조회
    @Query("SELECT w FROM WorkDiscussion w WHERE w.construction.id = :constructionId AND w.groupName = :groupName")
    Page<WorkDiscussion> findByConstructionIdAndGroupName(@Param("constructionId") Long constructionId, @Param("groupName") String groupName, Pageable pageable);
    
    // 제목으로 검색
    Page<WorkDiscussion> findByTitleContaining(String title, Pageable pageable);
    
    // 제목과 그룹으로 검색
    Page<WorkDiscussion> findByTitleContainingAndGroupName(String title, String groupName, Pageable pageable);
    
    // 제목과 건설현장 ID로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.title LIKE %:title% AND w.construction.id = :constructionId")
    Page<WorkDiscussion> findByTitleContainingAndConstructionId(@Param("title") String title, @Param("constructionId") Long constructionId, Pageable pageable);
    
    // 제목, 건설현장 ID, 그룹으로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.title LIKE %:title% AND w.construction.id = :constructionId AND w.groupName = :groupName")
    Page<WorkDiscussion> findByTitleContainingAndConstructionIdAndGroupName(
            @Param("title") String title, 
            @Param("constructionId") Long constructionId, 
            @Param("groupName") String groupName, 
            Pageable pageable);
    
    // 날짜 범위로 검색
    Page<WorkDiscussion> findByDiscussionDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
    // 날짜 범위와 그룹으로 검색
    Page<WorkDiscussion> findByDiscussionDateBetweenAndGroupName(
            LocalDateTime startDate, 
            LocalDateTime endDate, 
            String groupName, 
            Pageable pageable);
    
    // 날짜 범위와 건설현장 ID로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.discussionDate BETWEEN :startDate AND :endDate AND w.construction.id = :constructionId")
    Page<WorkDiscussion> findByDiscussionDateBetweenAndConstructionId(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("constructionId") Long constructionId, 
            Pageable pageable);
    
    // 날짜 범위, 건설현장 ID, 그룹으로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.discussionDate BETWEEN :startDate AND :endDate AND w.construction.id = :constructionId AND w.groupName = :groupName")
    Page<WorkDiscussion> findByDiscussionDateBetweenAndConstructionIdAndGroupName(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("constructionId") Long constructionId, 
            @Param("groupName") String groupName, 
            Pageable pageable);
    
    // 제목과 날짜 범위로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.title LIKE %:title% AND w.discussionDate BETWEEN :startDate AND :endDate")
    Page<WorkDiscussion> findByTitleContainingAndDiscussionDateBetween(
            @Param("title") String title, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            Pageable pageable);
    
    // 제목, 날짜 범위, 그룹으로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.title LIKE %:title% AND w.discussionDate BETWEEN :startDate AND :endDate AND w.groupName = :groupName")
    Page<WorkDiscussion> findByTitleContainingAndDiscussionDateBetweenAndGroupName(
            @Param("title") String title, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("groupName") String groupName, 
            Pageable pageable);
    
    // 제목, 날짜 범위, 건설현장 ID로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.title LIKE %:title% AND w.discussionDate BETWEEN :startDate AND :endDate AND w.construction.id = :constructionId")
    Page<WorkDiscussion> findByTitleContainingAndDiscussionDateBetweenAndConstructionId(
            @Param("title") String title, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("constructionId") Long constructionId, 
            Pageable pageable);
    
    // 제목, 날짜 범위, 건설현장 ID, 그룹으로 검색
    @Query("SELECT w FROM WorkDiscussion w WHERE w.title LIKE %:title% AND w.discussionDate BETWEEN :startDate AND :endDate AND w.construction.id = :constructionId AND w.groupName = :groupName")
    Page<WorkDiscussion> findByTitleContainingAndDiscussionDateBetweenAndConstructionIdAndGroupName(
            @Param("title") String title, 
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate, 
            @Param("constructionId") Long constructionId, 
            @Param("groupName") String groupName, 
            Pageable pageable);
} 