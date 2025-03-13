package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConstructionJpaRepository extends JpaRepository<Construction, Long> {
    Page<Construction> findAllByOrderByIdDesc(Pageable pageable);
    
    @Query("SELECT c FROM Construction c LEFT JOIN FETCH c.managementMenus WHERE c.id = :id")
    Construction findByIdWithManagementMenus(@Param("id") Long id);
    
    // 검색 기능을 위한 JPQL 메소드 추가 (LOWER와 CONCAT 사용)
    @Query("SELECT c FROM Construction c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY c.id DESC")
    Page<Construction> findByNameContainingOrderByIdDesc(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT c FROM Construction c WHERE LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')) ORDER BY c.id DESC")
    Page<Construction> findByLocationContainingOrderByIdDesc(@Param("location") String location, Pageable pageable);
    
    @Query("SELECT c FROM Construction c WHERE LOWER(c.status) LIKE LOWER(CONCAT('%', :status, '%')) ORDER BY c.id DESC")
    Page<Construction> findByStatusContainingOrderByIdDesc(@Param("status") String status, Pageable pageable);
    
    @Query("SELECT c FROM Construction c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')) AND " +
           "LOWER(c.status) LIKE LOWER(CONCAT('%', :status, '%')) " +
           "ORDER BY c.id DESC")
    Page<Construction> findByNameContainingAndLocationContainingAndStatusContainingOrderByIdDesc(
            @Param("name") String name, 
            @Param("location") String location, 
            @Param("status") String status, 
            Pageable pageable);
    
    @Query("SELECT c FROM Construction c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')) " +
           "ORDER BY c.id DESC")
    Page<Construction> findByNameContainingAndLocationContainingOrderByIdDesc(
            @Param("name") String name, 
            @Param("location") String location, 
            Pageable pageable);
    
    @Query("SELECT c FROM Construction c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND " +
           "LOWER(c.status) LIKE LOWER(CONCAT('%', :status, '%')) " +
           "ORDER BY c.id DESC")
    Page<Construction> findByNameContainingAndStatusContainingOrderByIdDesc(
            @Param("name") String name, 
            @Param("status") String status, 
            Pageable pageable);
    
    @Query("SELECT c FROM Construction c WHERE " +
           "LOWER(c.location) LIKE LOWER(CONCAT('%', :location, '%')) AND " +
           "LOWER(c.status) LIKE LOWER(CONCAT('%', :status, '%')) " +
           "ORDER BY c.id DESC")
    Page<Construction> findByLocationContainingAndStatusContainingOrderByIdDesc(
            @Param("location") String location, 
            @Param("status") String status, 
            Pageable pageable);
}
