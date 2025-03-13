package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RequestConstructionJpaRepository extends JpaRepository<RequestConstruction, Long> {
    Page<RequestConstruction> findAllByOrderByIdDesc(Pageable pageable);
    
    @Query("SELECT r FROM RequestConstruction r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY r.id DESC")
    Page<RequestConstruction> findByNameContainingOrderByIdDesc(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT r FROM RequestConstruction r WHERE LOWER(r.phone) LIKE LOWER(CONCAT('%', :phone, '%')) ORDER BY r.id DESC")
    Page<RequestConstruction> findByPhoneContainingOrderByIdDesc(@Param("phone") String phone, Pageable pageable);
    
    @Query("SELECT r FROM RequestConstruction r WHERE LOWER(r.constructionLocation) LIKE LOWER(CONCAT('%', :location, '%')) ORDER BY r.id DESC")
    Page<RequestConstruction> findByConstructionLocationContainingOrderByIdDesc(@Param("location") String location, Pageable pageable);
}
