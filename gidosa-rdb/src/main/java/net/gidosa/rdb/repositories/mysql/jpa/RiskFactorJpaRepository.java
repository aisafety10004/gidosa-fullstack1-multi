package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.RiskFactor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

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
            
    // Advanced search with multiple criteria
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId " +
           "AND (:siteName IS NULL OR :siteName = '' OR rf.siteName LIKE CONCAT('%', :siteName, '%')) " +
           "AND (:executionDateStart IS NULL OR rf.executionDate >= :executionDateStart) " +
           "AND (:executionDateEnd IS NULL OR rf.executionDate <= :executionDateEnd) " +
           "AND (:workProcess IS NULL OR :workProcess = '' OR rf.workProcess LIKE CONCAT('%', :workProcess, '%')) " +
           "AND (:workLocation IS NULL OR :workLocation = '' OR rf.workLocation LIKE CONCAT('%', :workLocation, '%')) " +
           "AND (:riskClassification IS NULL OR rf.riskClassification = :riskClassification) " +
           "AND (:riskDetailFactor IS NULL OR :riskDetailFactor = '' OR rf.riskDetailFactor LIKE CONCAT('%', :riskDetailFactor, '%')) " +
           "AND (:impResult IS NULL OR :impResult = '' OR rf.impResult LIKE CONCAT('%', :impResult, '%'))")
    Page<RiskFactor> advancedSearch(
            @Param("constructionId") Long constructionId,
            @Param("siteName") String siteName,
            @Param("executionDateStart") LocalDate executionDateStart,
            @Param("executionDateEnd") LocalDate executionDateEnd,
            @Param("workProcess") String workProcess,
            @Param("workLocation") String workLocation,
            @Param("riskClassification") RiskFactor.RiskClassification riskClassification,
            @Param("riskDetailFactor") String riskDetailFactor,
            @Param("impResult") String impResult,
            Pageable pageable);
            
    // For Excel/CSV export - same query as advancedSearch but returns List instead of Page
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId " +
           "AND (:siteName IS NULL OR :siteName = '' OR rf.siteName LIKE CONCAT('%', :siteName, '%')) " +
           "AND (:executionDateStart IS NULL OR rf.executionDate >= :executionDateStart) " +
           "AND (:executionDateEnd IS NULL OR rf.executionDate <= :executionDateEnd) " +
           "AND (:workProcess IS NULL OR :workProcess = '' OR rf.workProcess LIKE CONCAT('%', :workProcess, '%')) " +
           "AND (:workLocation IS NULL OR :workLocation = '' OR rf.workLocation LIKE CONCAT('%', :workLocation, '%')) " +
           "AND (:riskClassification IS NULL OR rf.riskClassification = :riskClassification) " +
           "AND (:riskDetailFactor IS NULL OR :riskDetailFactor = '' OR rf.riskDetailFactor LIKE CONCAT('%', :riskDetailFactor, '%')) " +
           "AND (:impResult IS NULL OR :impResult = '' OR rf.impResult LIKE CONCAT('%', :impResult, '%')) " +
           "ORDER BY rf.id DESC")
    List<RiskFactor> findAllForExport(
            @Param("constructionId") Long constructionId,
            @Param("siteName") String siteName,
            @Param("executionDateStart") LocalDate executionDateStart,
            @Param("executionDateEnd") LocalDate executionDateEnd,
            @Param("workProcess") String workProcess,
            @Param("workLocation") String workLocation,
            @Param("riskClassification") RiskFactor.RiskClassification riskClassification,
            @Param("riskDetailFactor") String riskDetailFactor,
            @Param("impResult") String impResult);
}