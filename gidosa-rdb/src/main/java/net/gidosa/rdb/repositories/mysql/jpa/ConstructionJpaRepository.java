package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConstructionJpaRepository extends JpaRepository<Construction, Long> {
    Page<Construction> findAllByOrderByIdDesc(Pageable pageable);
}
