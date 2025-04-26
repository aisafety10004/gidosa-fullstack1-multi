package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.CustomHtmlPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CustomHtmlPageJpaRepository extends JpaRepository<CustomHtmlPage, Long> {
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.htmlFile WHERE h.id = :id")
    Optional<CustomHtmlPage> findByIdWithHtmlFile(@Param("id") Long id);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.id = :id")
    Optional<CustomHtmlPage> findByIdWithConstruction(@Param("id") Long id);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.htmlFile LEFT JOIN FETCH h.construction WHERE h.id = :id")
    Optional<CustomHtmlPage> findByIdWithHtmlFileAndConstruction(@Param("id") Long id);
    
    @Query("SELECT DISTINCT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction")
    List<CustomHtmlPage> findAllWithConstruction();
    
    @Query(value = "SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction",
           countQuery = "SELECT COUNT(h) FROM CustomHtmlPage h")
    Page<CustomHtmlPage> findAllWithConstructionPage(Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.title LIKE %:title%")
    Page<CustomHtmlPage> findByTitleContainingWithConstruction(@Param("title") String title, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.published = :published")
    Page<CustomHtmlPage> findByPublishedWithConstruction(@Param("published") Boolean published, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.createdAt BETWEEN :startDate AND :endDate")
    Page<CustomHtmlPage> findByCreatedAtBetweenWithConstruction(@Param("startDate") LocalDateTime startDate,
                                                                @Param("endDate") LocalDateTime endDate,
                                                                Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.title LIKE %:title% AND h.createdAt BETWEEN :startDate AND :endDate")
    Page<CustomHtmlPage> findByTitleContainingAndCreatedAtBetweenWithConstruction(@Param("title") String title,
                                                                                  @Param("startDate") LocalDateTime startDate,
                                                                                  @Param("endDate") LocalDateTime endDate,
                                                                                  Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.title LIKE %:title% AND h.published = :published")
    Page<CustomHtmlPage> findByTitleContainingAndPublishedWithConstruction(@Param("title") String title,
                                                                           @Param("published") Boolean published,
                                                                           Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.createdAt BETWEEN :startDate AND :endDate AND h.published = :published")
    Page<CustomHtmlPage> findByCreatedAtBetweenAndPublishedWithConstruction(@Param("startDate") LocalDateTime startDate,
                                                                            @Param("endDate") LocalDateTime endDate,
                                                                            @Param("published") Boolean published,
                                                                            Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.title LIKE %:title% AND h.createdAt BETWEEN :startDate AND :endDate AND h.published = :published")
    Page<CustomHtmlPage> findByTitleContainingAndCreatedAtBetweenAndPublishedWithConstruction(@Param("title") String title,
                                                                                              @Param("startDate") LocalDateTime startDate,
                                                                                              @Param("endDate") LocalDateTime endDate,
                                                                                              @Param("published") Boolean published,
                                                                                              Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.construction.id = :constructionId")
    Page<CustomHtmlPage> findByConstructionIdWithConstruction(@Param("constructionId") Long constructionId, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.construction IS NULL")
    Page<CustomHtmlPage> findByConstructionIsNull(Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.construction IS NULL OR h.construction.id = :constructionId")
    Page<CustomHtmlPage> findByConstructionNullOrIdWithConstruction(@Param("constructionId") Long constructionId, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.title LIKE %:title% AND (h.construction.id = :constructionId)")
    Page<CustomHtmlPage> findByTitleAndConstructionIdWithConstruction(@Param("title") String title, @Param("constructionId") Long constructionId, Pageable pageable);

    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.title LIKE %:title% AND (h.construction.id = :constructionId OR h.construction IS NULL)")
    Page<CustomHtmlPage> findByTitleAndConstructionIdOrNullWithConstruction(@Param("title") String title, @Param("constructionId") Long constructionId, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.title LIKE %:title% AND h.construction IS NULL")
    Page<CustomHtmlPage> findByTitleAndConstructionIsNullWithConstruction(@Param("title") String title, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.createdAt BETWEEN :startDate AND :endDate AND (h.construction.id = :constructionId)")
    Page<CustomHtmlPage> findByDateRangeAndConstructionIdWithConstruction(@Param("startDate") LocalDateTime startDate,
                                                                          @Param("endDate") LocalDateTime endDate,
                                                                          @Param("constructionId") Long constructionId,
                                                                          Pageable pageable);

    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.createdAt BETWEEN :startDate AND :endDate AND (h.construction.id = :constructionId OR h.construction IS NULL)")
    Page<CustomHtmlPage> findByDateRangeAndConstructionIdOrNullWithConstruction(@Param("startDate") LocalDateTime startDate,
                                                                                @Param("endDate") LocalDateTime endDate,
                                                                                @Param("constructionId") Long constructionId,
                                                                                Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.published = :published AND (h.construction.id = :constructionId)")
    Page<CustomHtmlPage> findByPublishedAndConstructionIdWithConstruction(@Param("published") Boolean published,
                                                                          @Param("constructionId") Long constructionId,
                                                                          Pageable pageable);

    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.construction WHERE h.published = :published AND (h.construction.id = :constructionId OR h.construction IS NULL)")
    Page<CustomHtmlPage> findByPublishedAndConstructionIdOrNullWithConstruction(@Param("published") Boolean published,
                                                                                @Param("constructionId") Long constructionId,
                                                                                Pageable pageable);

    @Query("SELECT hp FROM CustomHtmlPage hp LEFT JOIN FETCH hp.construction WHERE hp.isMainPage = :isMainPage")
    List<CustomHtmlPage> findByIsMainPage(@Param("isMainPage") Boolean isMainPage);
    
    @Query("SELECT hp FROM CustomHtmlPage hp LEFT JOIN FETCH hp.construction WHERE hp.isMainPage = :isMainPage AND hp.construction.id = :constructionId")
    List<CustomHtmlPage> findByIsMainPageAndConstructionId(@Param("isMainPage") Boolean isMainPage, @Param("constructionId") Long constructionId);
    
    @Query("SELECT hp FROM CustomHtmlPage hp LEFT JOIN FETCH hp.construction WHERE hp.isMainPage = :isMainPage AND hp.published = :published")
    List<CustomHtmlPage> findByIsMainPageAndPublished(@Param("isMainPage") Boolean isMainPage, @Param("published") Boolean published);
} 