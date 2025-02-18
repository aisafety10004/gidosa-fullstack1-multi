package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberAdminJpaRepository extends JpaRepository<MemberAdmin, Long> {
    Optional<MemberAdmin> findByUsername(String username);
    Optional<MemberAdmin> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<MemberAdmin> findAllByOrderByIdDesc(Pageable pageable);
    Page<MemberAdmin> findByLocation(String location, Pageable pageable);
    long countByLocation(String location);
}
