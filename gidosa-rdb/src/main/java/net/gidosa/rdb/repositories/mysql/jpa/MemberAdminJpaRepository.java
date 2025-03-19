package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberAdminJpaRepository extends JpaRepository<MemberAdmin, Long>, PagingAndSortingRepository<MemberAdmin, Long> {
    Optional<MemberAdmin> findByUsername(String username);
    Optional<MemberAdmin> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<MemberAdmin> findAllByOrderByIdDesc(Pageable pageable);
//    @EntityGraph(attributePaths = "construction")
//    Page<MemberAdmin> findAllByOrderByIdDesc(Pageable pageable);
    Page<MemberAdmin> findByLocation(String location, Pageable pageable);
    long countByLocation(String location);

    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction")
    Page<MemberAdmin> findAllWithConstruction(Pageable pageable);

    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction WHERE m.id = :id")
    Optional<MemberAdmin> findByIdWithConstruction(@Param("id") Long id);

    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction WHERE m.username = :username")
    Optional<MemberAdmin> findByUsernameWithConstruction(@Param("username") String username);

    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction c LEFT JOIN FETCH c.managementMenus WHERE m.username = :username")
    Optional<MemberAdmin> findByUsernameWithConstructionAndManagementMenus(@Param("username") String username);

    /**
     * 특정 건설 현장의 활성화된 관리자 목록을 조회합니다.
     */
    List<MemberAdmin> findByConstructionIdAndIsActiveTrue(Long constructionId);
    
    // 검색 기능을 위한 메서드 추가
    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction WHERE m.username LIKE %:keyword% ORDER BY m.id DESC")
    Page<MemberAdmin> findByUsernameContainingOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction WHERE m.name LIKE %:keyword% ORDER BY m.id DESC")
    Page<MemberAdmin> findByNameContainingOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction WHERE m.email LIKE %:keyword% ORDER BY m.id DESC")
    Page<MemberAdmin> findByEmailContainingOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT m FROM MemberAdmin m LEFT JOIN FETCH m.construction WHERE m.phone LIKE %:keyword% ORDER BY m.id DESC")
    Page<MemberAdmin> findByPhoneContainingOrderByIdDesc(@Param("keyword") String keyword, Pageable pageable);
}
