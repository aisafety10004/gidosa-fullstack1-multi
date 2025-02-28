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
}
