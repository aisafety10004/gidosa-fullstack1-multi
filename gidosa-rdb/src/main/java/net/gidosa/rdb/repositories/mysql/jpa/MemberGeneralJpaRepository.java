package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberGeneralJpaRepository extends JpaRepository<MemberGeneral, Long> {
    Optional<MemberGeneral> findByUsername(String username);
    Optional<MemberGeneral> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<MemberGeneral> findAllByOrderByIdDesc(Pageable pageable);
    Page<MemberGeneral> findByConstructionId(Long constructionId, Pageable pageable);
    Optional<MemberGeneral> findByNameAndEmail(String name, String email);
    Optional<MemberGeneral> findByNameAndEmailAndConstructionId(String name, String email, Long constructionId);

    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.username = :username")
    Optional<MemberGeneral> findByUsernameWithConstruction(@Param("username") String username);
}
