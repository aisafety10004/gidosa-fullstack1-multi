package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberGeneralJpaRepository extends JpaRepository<MemberGeneral, Long> {
    Optional<MemberGeneral> findByUsername(String username);
    Optional<MemberGeneral> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<MemberGeneral> findAllByOrderByIdDesc(Pageable pageable);
}
