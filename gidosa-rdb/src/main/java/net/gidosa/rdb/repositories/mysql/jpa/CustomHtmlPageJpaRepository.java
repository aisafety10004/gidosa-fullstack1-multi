package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.CustomHtmlPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomHtmlPageJpaRepository extends JpaRepository<CustomHtmlPage, Long> {
    
    @Query("SELECT h FROM CustomHtmlPage h LEFT JOIN FETCH h.htmlFile WHERE h.id = :id")
    Optional<CustomHtmlPage> findByIdWithHtmlFile(@Param("id") Long id);
    
    @Query("SELECT h FROM CustomHtmlPage h WHERE h.title LIKE %:title%")
    Page<CustomHtmlPage> findByTitleContaining(@Param("title") String title, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h WHERE h.published = :published")
    Page<CustomHtmlPage> findByPublished(@Param("published") Boolean published, Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h WHERE h.createdAt BETWEEN :startDate AND :endDate")
    Page<CustomHtmlPage> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate, 
                                              Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h WHERE h.title LIKE %:title% AND h.createdAt BETWEEN :startDate AND :endDate")
    Page<CustomHtmlPage> findByTitleContainingAndCreatedAtBetween(@Param("title") String title, 
                                                                @Param("startDate") LocalDateTime startDate, 
                                                                @Param("endDate") LocalDateTime endDate, 
                                                                Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h WHERE h.title LIKE %:title% AND h.published = :published")
    Page<CustomHtmlPage> findByTitleContainingAndPublished(@Param("title") String title, 
                                                         @Param("published") Boolean published, 
                                                         Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h WHERE h.createdAt BETWEEN :startDate AND :endDate AND h.published = :published")
    Page<CustomHtmlPage> findByCreatedAtBetweenAndPublished(@Param("startDate") LocalDateTime startDate, 
                                                          @Param("endDate") LocalDateTime endDate, 
                                                          @Param("published") Boolean published, 
                                                          Pageable pageable);
    
    @Query("SELECT h FROM CustomHtmlPage h WHERE h.title LIKE %:title% AND h.createdAt BETWEEN :startDate AND :endDate AND h.published = :published")
    Page<CustomHtmlPage> findByTitleContainingAndCreatedAtBetweenAndPublished(@Param("title") String title, 
                                                                            @Param("startDate") LocalDateTime startDate, 
                                                                            @Param("endDate") LocalDateTime endDate, 
                                                                            @Param("published") Boolean published, 
                                                                            Pageable pageable);
} 