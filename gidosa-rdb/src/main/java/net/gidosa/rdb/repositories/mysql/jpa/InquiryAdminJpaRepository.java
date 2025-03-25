package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.InquiryAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InquiryAdminJpaRepository extends JpaRepository<InquiryAdmin, Long> {
    // 기본적인 CRUD 작업은 JpaRepository에서 제공
    
    // ID 기준 내림차순 정렬된 페이징 처리된 문의사항 목록 조회
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.fileAttachment1",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i")
    Page<InquiryAdmin> findAllByOrderByIdDesc(Pageable pageable);
    
    // 특정 건설현장 ID에 해당하는 문의사항 조회
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId ORDER BY i.id DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId")
    Page<InquiryAdmin> findByConstructionIdOrderByIdDesc(@Param("constructionId") Long constructionId, Pageable pageable);

    // 특정 문의사항 조회 - 첨부파일 포함
    @Query("SELECT i FROM InquiryAdmin i " +
           "LEFT JOIN FETCH i.fileAttachment1 " +
           "LEFT JOIN FETCH i.fileAttachment2 " +
           "LEFT JOIN FETCH i.fileAttachment3 " +
           "WHERE i.id = :id")
    Optional<InquiryAdmin> findByIdWithAttachments(@Param("id") Long id);

    // 특정 문의사항 조회 - 첨부파일과 건설현장 정보 모두 포함
    @Query("SELECT i FROM InquiryAdmin i " +
           "LEFT JOIN FETCH i.construction " +
           "LEFT JOIN FETCH i.fileAttachment1 " +
           "LEFT JOIN FETCH i.fileAttachment2 " +
           "LEFT JOIN FETCH i.fileAttachment3 " +
           "WHERE i.id = :id")
    Optional<InquiryAdmin> findByIdWithAttachmentsAndConstruction(@Param("id") Long id);

    // 특정 건설현장 ID에 해당하는 문의사항 조회 - Construction 정보 포함
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId ORDER BY i.id DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId")
    Page<InquiryAdmin> findByConstructionIdWithConstructionOrderByIdDesc(@Param("constructionId") Long constructionId, Pageable pageable);
    
    // 모든 문의사항 조회 - Construction 정보 포함
    @Query("SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 ORDER BY i.id DESC")
    List<InquiryAdmin> findAllWithConstructionOrderByIdDesc();
    
    // ID 기준 내림차순 정렬된 페이징 처리된 문의사항 목록 조회 - Construction 정보 포함
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i")
    Page<InquiryAdmin> findAllWithConstructionOrderByIdDesc(Pageable pageable);
    
    // 제목으로 문의사항 검색 (특정 건설현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<InquiryAdmin> findByConstructionIdAndTitleContainingIgnoreCase(@Param("constructionId") Long constructionId, @Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 문의일자 범위로 문의사항 검색 (특정 건설현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId AND i.inquiryDate BETWEEN :startDate AND :endDate ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId AND i.inquiryDate BETWEEN :startDate AND :endDate")
    Page<InquiryAdmin> findByConstructionIdAndInquiryDateBetween(@Param("constructionId") Long constructionId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 제목과 문의일자 범위로 문의사항 검색 (특정 건설현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND i.inquiryDate BETWEEN :startDate AND :endDate ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND i.inquiryDate BETWEEN :startDate AND :endDate")
    Page<InquiryAdmin> findByConstructionIdAndTitleContainingIgnoreCaseAndInquiryDateBetween(@Param("constructionId") Long constructionId, @Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 답변 여부로 문의사항 검색 (특정 건설현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId AND i.answered = :answered ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId AND i.answered = :answered")
    Page<InquiryAdmin> findByConstructionIdAndAnswered(@Param("constructionId") Long constructionId, @Param("answered") Boolean answered, Pageable pageable);
    
    // 답변 여부와 제목으로 문의사항 검색 (특정 건설현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId AND i.answered = :answered AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId AND i.answered = :answered AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<InquiryAdmin> findByConstructionIdAndAnsweredAndTitleContainingIgnoreCase(@Param("constructionId") Long constructionId, @Param("answered") Boolean answered, @Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 문의 유형으로 문의사항 검색 (특정 건설현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId AND i.inquiryType = :inquiryType ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId AND i.inquiryType = :inquiryType")
    Page<InquiryAdmin> findByConstructionIdAndInquiryType(@Param("constructionId") Long constructionId, @Param("inquiryType") String inquiryType, Pageable pageable);
    
    // 문의 유형과 제목으로 문의사항 검색 (특정 건설현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.construction.id = :constructionId AND i.inquiryType = :inquiryType AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.construction.id = :constructionId AND i.inquiryType = :inquiryType AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<InquiryAdmin> findByConstructionIdAndInquiryTypeAndTitleContainingIgnoreCase(@Param("constructionId") Long constructionId, @Param("inquiryType") String inquiryType, @Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 아래는 전체 현장에 대한 검색 메소드들
    
    // 제목으로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<InquiryAdmin> findAllWithTitleContainingIgnoreCase(@Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 현장명으로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%'))")
    Page<InquiryAdmin> findAllWithConstructionNameContainingIgnoreCase(@Param("searchConstructionName") String searchConstructionName, Pageable pageable);
    
    // 현장명과 제목으로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<InquiryAdmin> findAllWithConstructionNameAndTitleContainingIgnoreCase(@Param("searchConstructionName") String searchConstructionName, @Param("searchTitle") String searchTitle, Pageable pageable);
    
    // 현장명과 날짜 범위로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND i.inquiryDate BETWEEN :startDate AND :endDate ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND i.inquiryDate BETWEEN :startDate AND :endDate")
    Page<InquiryAdmin> findAllWithConstructionNameAndInquiryDateBetween(@Param("searchConstructionName") String searchConstructionName, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 현장명, 제목, 날짜 범위로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND i.inquiryDate BETWEEN :startDate AND :endDate ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND i.inquiryDate BETWEEN :startDate AND :endDate")
    Page<InquiryAdmin> findAllWithConstructionNameAndTitleAndInquiryDateBetween(@Param("searchConstructionName") String searchConstructionName, @Param("searchTitle") String searchTitle, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);
    
    // 현장명과 답변 여부로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND i.answered = :answered ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND i.answered = :answered")
    Page<InquiryAdmin> findAllWithConstructionNameAndAnswered(@Param("searchConstructionName") String searchConstructionName, @Param("answered") Boolean answered, Pageable pageable);
    
    // 현장명, 제목, 답변 여부로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND i.answered = :answered ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE LOWER(i.construction.name) LIKE LOWER(CONCAT('%', :searchConstructionName, '%')) AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) AND i.answered = :answered")
    Page<InquiryAdmin> findAllWithConstructionNameAndTitleAndAnswered(@Param("searchConstructionName") String searchConstructionName, @Param("searchTitle") String searchTitle, @Param("answered") Boolean answered, Pageable pageable);
    
    // 답변 여부와 제목으로 문의사항 검색 (전체 현장)
    @Query(value = "SELECT i FROM InquiryAdmin i LEFT JOIN FETCH i.construction LEFT JOIN FETCH i.fileAttachment1 WHERE i.answered = :answered AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%')) ORDER BY i.inquiryDate DESC",
           countQuery = "SELECT COUNT(i) FROM InquiryAdmin i WHERE i.answered = :answered AND LOWER(i.title) LIKE LOWER(CONCAT('%', :searchTitle, '%'))")
    Page<InquiryAdmin> findAllWithAnsweredAndTitleContainingIgnoreCase(@Param("answered") Boolean answered, @Param("searchTitle") String searchTitle, Pageable pageable);
} 