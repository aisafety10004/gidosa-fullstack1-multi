package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.Notice;
import net.gidosa.rdb.models.entities.dbs.mysql.FileAttachment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {
    // 기본적인 CRUD 작업은 JpaRepository에서 제공
    
    // ID 기준 내림차순 정렬된 페이징 처리된 공지사항 목록 조회
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1",
           countQuery = "SELECT COUNT(n) FROM Notice n")
    Page<Notice> findAllByOrderByIdDesc(Pageable pageable);
    
    // 특정 건설현장 ID에 해당하는 공지사항 또는 모든 공지사항(construction_id가 null인 경우) 조회
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId OR n.construction IS NULL ORDER BY n.id DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId OR n.construction IS NULL")
    Page<Notice> findByConstructionIdOrConstructionIsNullOrderByIdDesc(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // construction_id가 null인 공지사항만 조회 (모든 사용자에게 보이는 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL ORDER BY n.id DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL")
    Page<Notice> findByConstructionIsNullOrderByIdDesc(Pageable pageable);

    @Query("SELECT n FROM Notice n " +
           "LEFT JOIN FETCH n.fileAttachment1 " +
           "LEFT JOIN FETCH n.fileAttachment2 " +
           "LEFT JOIN FETCH n.fileAttachment3 " +
//           "LEFT JOIN FETCH n.fileAttachment4 " +
//           "LEFT JOIN FETCH n.fileAttachment5 " +
           "WHERE n.id = :id")
    Optional<Notice> findByIdWithAttachments(@Param("id") Long id);

    // 특정 공지사항 조회 - 첨부파일과 건설현장 정보 모두 포함
    @Query("SELECT n FROM Notice n " +
           "LEFT JOIN FETCH n.construction " +
           "LEFT JOIN FETCH n.fileAttachment1 " +
           "LEFT JOIN FETCH n.fileAttachment2 " +
           "LEFT JOIN FETCH n.fileAttachment3 " +
//           "LEFT JOIN FETCH n.fileAttachment4 " +
//           "LEFT JOIN FETCH n.fileAttachment5 " +
           "WHERE n.id = :id")
    Optional<Notice> findByIdWithAttachmentsAndConstruction(@Param("id") Long id);

    // 특정 건설현장 ID에 해당하는 공지사항 또는 모든 공지사항(construction_id가 null인 경우) 조회 - Construction 정보 포함
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId OR n.construction IS NULL ORDER BY n.id DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId OR n.construction IS NULL")
    Page<Notice> findByConstructionIdOrConstructionIsNullWithConstructionOrderByIdDesc(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // 모든 공지사항 조회 - Construction 정보 포함
    @Query("SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 ORDER BY n.id DESC")
    List<Notice> findAllWithConstructionOrderByIdDesc();
    
    // ID 기준 내림차순 정렬된 페이징 처리된 공지사항 목록 조회 - Construction 정보 포함
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1",
           countQuery = "SELECT COUNT(n) FROM Notice n")
    Page<Notice> findAllWithConstructionOrderByIdDesc(Pageable pageable);
    
    // 제목으로 공지사항 검색 (모든 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findByTitleContainingIgnoreCase(@Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 공지일자 범위로 공지사항 검색 (모든 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findByNoticeDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목과 공지일자 범위로 공지사항 검색 (모든 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findByTitleContainingIgnoreCaseAndNoticeDateBetween(@Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목으로 공지사항 검색 (특정 건설현장 또는 전체 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findByConstructionIdOrConstructionIsNullAndTitleContainingIgnoreCase(@Param("constructionId") Long constructionId, @Param("searchTitle") String searchTitle, Pageable pageable);

    // 제목으로 공지사항 검색 (특정 건설현장 또는 전체 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.publishedManager = false ORDER BY n.noticeDate DESC",
            countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findUnpublishedManagerByConstructionIdAndTitleContainingIgnoreCase(@Param("constructionId") Long constructionId, @Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 공지일자 범위로 공지사항 검색 (특정 건설현장 또는 전체 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findByConstructionIdOrConstructionIsNullAndNoticeDateBetween(@Param("constructionId") Long constructionId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목과 공지일자 범위로 공지사항 검색 (특정 건설현장 또는 전체 공지사항)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findByConstructionIdOrConstructionIsNullAndTitleContainingIgnoreCaseAndNoticeDateBetween(@Param("constructionId") Long constructionId, @Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 특정 건설현장 ID에 해당하는 공지사항 또는 모든 공지사항(construction_id가 null인 경우) 중 게시된 공지사항만 조회
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.publishedManager = true ORDER BY n.id DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.publishedManager = true")
    Page<Notice> findPublishedManagerByConstructionIdOrConstructionIsNullOrderByIdDesc(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // 특정 건설현장 ID에 해당하는 공지사항 또는 모든 공지사항(construction_id가 null인 경우) 중 게시되지 않은 공지사항만 조회
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedManager = false ORDER BY n.id DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedManager = false")
    Page<Notice> findUnpublishedManagerByConstructionIdOrderByIdDesc(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // 제목으로 공지사항 검색 (특정 건설현장 또는 전체 공지사항) - 게시된 공지사항만
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.publishedManager = true ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.publishedManager = true")
    Page<Notice> findPublishedManagerByConstructionIdOrConstructionIsNullAndTitleContainingIgnoreCase(@Param("constructionId") Long constructionId, @Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 공지일자 범위로 공지사항 검색 (특정 건설현장 또는 전체 공지사항) - 게시된 공지사항만
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.noticeDate BETWEEN :startDate AND :endDate AND n.publishedManager = true ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.noticeDate BETWEEN :startDate AND :endDate AND n.publishedManager = true")
    Page<Notice> findPublishedManagerByConstructionIdOrConstructionIsNullAndNoticeDateBetween(@Param("constructionId") Long constructionId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목과 공지일자 범위로 공지사항 검색 (특정 건설현장 또는 전체 공지사항) - 게시된 공지사항만
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate AND n.publishedManager = true ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate AND n.publishedManager = true")
    Page<Notice> findPublishedManagerByConstructionIdOrConstructionIsNullAndTitleContainingIgnoreCaseAndNoticeDateBetween(@Param("constructionId") Long constructionId, @Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // 제목으로 전역 공지사항 검색 (construction이 null인 경우만)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findByConstructionIsNullAndTitleContainingIgnoreCase(@Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 공지일자 범위로 전역 공지사항 검색 (construction이 null인 경우만)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findByConstructionIsNullAndNoticeDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목과 공지일자 범위로 전역 공지사항 검색 (construction이 null인 경우만)
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findByConstructionIsNullAndTitleContainingIgnoreCaseAndNoticeDateBetween(@Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 익명 사용자에게 보이는 공지사항 조회 - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedAnonymous = true",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedAnonymous = true")
    Page<Notice> findPublishedAnonymous(Pageable pageable);
    
    // 제목으로 익명 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedAnonymous = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedAnonymous = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findPublishedAnonymousByTitleContainingIgnoreCase(@Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 공지일자 범위로 익명 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedAnonymous = true AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedAnonymous = true AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findPublishedAnonymousByNoticeDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목과 공지일자 범위로 익명 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedAnonymous = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedAnonymous = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findPublishedAnonymousByTitleContainingIgnoreCaseAndNoticeDateBetween(@Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 로그인 사용자에게 보이는 공지사항 조회 - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedLoggedInUser = true",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedLoggedInUser = true")
    Page<Notice> findPublishedLoggedInUser(Pageable pageable);
    
    // 제목으로 로그인 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedLoggedInUser = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedLoggedInUser = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findPublishedLoggedInUserByTitleContainingIgnoreCase(@Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 공지일자 범위로 로그인 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedLoggedInUser = true AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedLoggedInUser = true AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findPublishedLoggedInUserByNoticeDateBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목과 공지일자 범위로 로그인 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedLoggedInUser = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedLoggedInUser = true AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findPublishedLoggedInUserByTitleContainingIgnoreCaseAndNoticeDateBetween(@Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // 매니저에게 보이는 공지사항 조회 (특정 건설현장의 게시된 공지사항) - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.publishedManager = true",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE (n.construction.id = :constructionId OR n.construction IS NULL) AND n.publishedManager = true")
    Page<Notice> findPublishedManagerByConstructionIdOrConstructionIsNull(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // 특정 건설현장의 미게시 공지사항 조회 - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.construction LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedManager = false",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedManager = false")
    Page<Notice> findUnpublishedManagerByConstructionId(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // 전체 공지사항 조회 (construction이 null인 경우) - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL")
    Page<Notice> findGlobalNotices(Pageable pageable);

    // 제목으로 전체 공지사항 검색 (construction이 null인 경우) - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findGlobalNoticesByTitle(@Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 제목과 날짜로 전체 공지사항 검색 (construction이 null인 경우) - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findGlobalNoticesByTitleAndDateRange(@Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 날짜로 전체 공지사항 검색 (construction이 null인 경우) - 동적 정렬 지원
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction IS NULL AND n.noticeDate BETWEEN :startDate AND :endDate",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction IS NULL AND n.noticeDate BETWEEN :startDate AND :endDate")
    Page<Notice> findGlobalNoticesByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    // 내용으로 익명 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedAnonymous = true AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedAnonymous = true AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%'))")
    Page<Notice> findPublishedAnonymousByContentContainingIgnoreCase(@Param("searchContent") String searchContent, Pageable pageable);
    
    // 내용으로 로그인 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.publishedLoggedInUser = true AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.publishedLoggedInUser = true AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%'))")
    Page<Notice> findPublishedLoggedInUserByContentContainingIgnoreCase(@Param("searchContent") String searchContent, Pageable pageable);
    
    // 특정 건설현장에 대한 익명 사용자에게 보이는 공지사항 조회
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedAnonymous = :published ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedAnonymous = :published")
    Page<Notice> findByConstructionIdAndPublishedAnonymous(
            @Param("constructionId") Long constructionId, 
            @Param("published") boolean published, 
            Pageable pageable);
    
    // 특정 건설현장에 대한 로그인 사용자에게 보이는 공지사항 조회
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedLoggedInUser = :published ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedLoggedInUser = :published")
    Page<Notice> findByConstructionIdAndPublishedLoggedInUser(
            @Param("constructionId") Long constructionId, 
            @Param("published") boolean published, 
            Pageable pageable);
    
    // 제목으로 특정 건설현장에 대한 익명 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedAnonymous = :published AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedAnonymous = :published AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findByConstructionIdAndPublishedAnonymousAndTitleContainingIgnoreCase(
            @Param("constructionId") Long constructionId, 
            @Param("published") boolean published, 
            @Param("searchTitle") String searchTitle, 
            Pageable pageable);
    
    // 내용으로 특정 건설현장에 대한 익명 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedAnonymous = :published AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedAnonymous = :published AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%'))")
    Page<Notice> findByConstructionIdAndPublishedAnonymousAndContentContainingIgnoreCase(
            @Param("constructionId") Long constructionId, 
            @Param("published") boolean published, 
            @Param("searchContent") String searchContent, 
            Pageable pageable);
    
    // 제목으로 특정 건설현장에 대한 로그인 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedLoggedInUser = :published AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedLoggedInUser = :published AND LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<Notice> findByConstructionIdAndPublishedLoggedInUserAndTitleContainingIgnoreCase(
            @Param("constructionId") Long constructionId, 
            @Param("published") boolean published, 
            @Param("searchTitle") String searchTitle, 
            Pageable pageable);
    
    // 내용으로 특정 건설현장에 대한 로그인 사용자에게 보이는 공지사항 검색
    @Query(value = "SELECT n FROM Notice n LEFT JOIN FETCH n.fileAttachment1 WHERE n.construction.id = :constructionId AND n.publishedLoggedInUser = :published AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%')) ORDER BY n.noticeDate DESC",
           countQuery = "SELECT COUNT(n) FROM Notice n WHERE n.construction.id = :constructionId AND n.publishedLoggedInUser = :published AND LOWER(n.content) LIKE LOWER(CONCAT('%', :searchContent, '%'))")
    Page<Notice> findByConstructionIdAndPublishedLoggedInUserAndContentContainingIgnoreCase(
            @Param("constructionId") Long constructionId, 
            @Param("published") boolean published, 
            @Param("searchContent") String searchContent, 
            Pageable pageable);
} 