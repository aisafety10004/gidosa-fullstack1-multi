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
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId AND rf.name LIKE CONCAT('%', :name, '%')")
    Page<RiskFactor> findByConstructionIdAndNameContaining(
            @Param("constructionId") Long constructionId, 
            @Param("name") String name, 
            Pageable pageable);
    
    // Find risk factors by date range
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId " +
           "AND (rf.startDate <= :endDate AND rf.endDate >= :startDate)")
    Page<RiskFactor> findByConstructionIdAndDateRange(
            @Param("constructionId") Long constructionId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    // Find risk factors by name and date range
    @Query("SELECT rf FROM RiskFactor rf WHERE rf.construction.id = :constructionId " +
           "AND rf.name LIKE CONCAT('%', :name, '%') " +
           "AND (rf.startDate <= :endDate AND rf.endDate >= :startDate)")
    Page<RiskFactor> findByConstructionIdAndNameContainingAndDateRange(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    // Find risk factors by status
    Page<RiskFactor> findByConstructionIdAndStatus(Long constructionId, RiskFactor.RiskStatus status, Pageable pageable);
} 