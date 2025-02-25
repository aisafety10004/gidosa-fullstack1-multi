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
    Optional<MemberGeneral> findByUsernameAndEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<MemberGeneral> findByPhone(String phone);
    Optional<MemberGeneral> findByConstructionId(Long constructionId);

    Page<MemberGeneral> findAllByOrderByIdDesc(Pageable pageable);
    Page<MemberGeneral> findByConstructionId(Long constructionId, Pageable pageable);
    Optional<MemberGeneral> findByNameAndEmail(String name, String email);
    Optional<MemberGeneral> findByNameAndEmailAndConstructionId(String name, String email, Long constructionId);

    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.username = :username")
    Optional<MemberGeneral> findByUsernameWithConstruction(@Param("username") String username);
    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.id = :id")
    Optional<MemberGeneral> findByIdWithConstruction(@Param("id") Long id);

    Optional<MemberGeneral> findByConstructionIdAndUsername(Long constructionId, String username);
    Optional<MemberGeneral> findByConstructionIdAndEmail(Long constructionId, String email);
    Optional<MemberGeneral> findByConstructionIdAndPhone(Long constructionId, String phone);
    Optional<MemberGeneral> findByConstructionIdAndUsernameAndEmail(Long constructionId, String username, String email);
    Optional<MemberGeneral> findByConstructionIdAndUsernameAndPhone(Long constructionId, String username, String phone);    
}
