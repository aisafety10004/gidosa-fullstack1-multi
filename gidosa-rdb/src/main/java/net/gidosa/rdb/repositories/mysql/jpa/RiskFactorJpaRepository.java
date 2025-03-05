package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface RiskFactorJpaRepository extends JpaRepository<RiskFactor, Long> {
    
    // Find risk factors by construction ID
    Page<RiskFactor> findByConstructionId(Long constructionId, Pageable pageable);
    
    // Find risk factors by name containing the search term
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId AND rf.siteName LIKE CONCAT('%', :siteName, '%')")
    Page<RiskFactor> findByConstructionIdAndNameContaining(
            @Param("constructionId") Long constructionId, 
            @Param("siteName") String siteName,
            Pageable pageable);
    
    // Find risk factors by execution date range
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId " +
           "AND (rf.executionDate >= :executionDateStart AND rf.executionDate <= :executionDateEnd)")
    Page<RiskFactor> findByConstructionIdAndExecutionDateRange(
            @Param("constructionId") Long constructionId,
            @Param("executionDateStart") LocalDate executionDateStart,
            @Param("executionDateEnd") LocalDate executionDateEnd,
            Pageable pageable);
    
    // Find risk factors by name and execution date range
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId " +
           "AND rf.siteName LIKE CONCAT('%', :siteName, '%') " +
           "AND (rf.executionDate >= :executionDateStart AND rf.executionDate <= :executionDateEnd)")
    Page<RiskFactor> findByConstructionIdAndNameContainingAndExecutionDateRange(
            @Param("constructionId") Long constructionId,
            @Param("siteName") String siteName,
            @Param("executionDateStart") LocalDate executionDateStart,
            @Param("executionDateEnd") LocalDate executionDateEnd,
            Pageable pageable);
}