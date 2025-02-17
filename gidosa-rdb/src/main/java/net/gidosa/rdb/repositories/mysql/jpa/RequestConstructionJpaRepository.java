package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestConstructionJpaRepository extends JpaRepository<RequestConstruction, Long> {
    Page<RequestConstruction> findAllByOrderByIdDesc(Pageable pageable);
}
