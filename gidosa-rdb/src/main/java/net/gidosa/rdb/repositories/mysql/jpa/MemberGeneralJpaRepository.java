package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberGeneralJpaRepository extends JpaRepository<MemberGeneral, Long> {
    Optional<MemberGeneral> findByUsername(String username);
    Optional<MemberGeneral> findByEmail(String email);
    Optional<MemberGeneral> findByUsernameAndEmail(String username, String email);
    Optional<MemberGeneral> findByUsernameAndConstructionId(String username, Long constructionId);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<MemberGeneral> findByPhone(String phone);
    
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId")
    Optional<MemberGeneral> findByConstructionId(@Param("constructionId") Long constructionId);

    Page<MemberGeneral> findAllByOrderByIdDesc(Pageable pageable);
    
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId")
    Page<MemberGeneral> findByConstructionId(@Param("constructionId") Long constructionId, Pageable pageable);
    
    Optional<MemberGeneral> findByNameAndEmail(String name, String email);
    
    @Query("SELECT m FROM MemberGeneral m WHERE m.name = :name AND m.email = :email AND m.construction.id = :constructionId")
    Optional<MemberGeneral> findByNameAndEmailAndConstructionId(
            @Param("name") String name, 
            @Param("email") String email, 
            @Param("constructionId") Long constructionId);
            
    /**
     * ID로 회원을 조회하면서 모든 첨부파일 정보를 함께 가져옵니다.
     * @param id 회원 ID
     * @return 첨부파일 정보가 포함된 회원 정보
     */
    @Query("SELECT m FROM MemberGeneral m " +
           "LEFT JOIN FETCH m.profilePhoto " +
           "LEFT JOIN FETCH m.laborContract " +
           "LEFT JOIN FETCH m.safetyEducationCert " +
           "LEFT JOIN FETCH m.protectiveGearPledge " +
           "LEFT JOIN FETCH m.etcDoc1 " +
           "LEFT JOIN FETCH m.etcDoc2 " +
           "LEFT JOIN FETCH m.etcDoc3 " +
           "WHERE m.id = :id")
    Optional<MemberGeneral> findByIdWithAttachments(@Param("id") Long id);
    
    /**
     * 사용자명으로 회원을 조회하면서 모든 첨부파일 정보를 함께 가져옵니다.
     * @param username 사용자명
     * @return 첨부파일 정보가 포함된 회원 정보
     */
    @Query("SELECT m FROM MemberGeneral m " +
           "LEFT JOIN FETCH m.profilePhoto " +
           "LEFT JOIN FETCH m.laborContract " +
           "LEFT JOIN FETCH m.safetyEducationCert " +
           "LEFT JOIN FETCH m.protectiveGearPledge " +
           "LEFT JOIN FETCH m.etcDoc1 " +
           "LEFT JOIN FETCH m.etcDoc2 " +
           "LEFT JOIN FETCH m.etcDoc3 " +
           "WHERE m.username = :username")
    Optional<MemberGeneral> findByUsernameWithAttachments(@Param("username") String username);
    
    /**
     * 건설 현장 ID로 회원을 조회하면서 모든 첨부파일 정보를 함께 가져옵니다.
     * @param constructionId 건설 현장 ID
     * @return 첨부파일 정보가 포함된 회원 목록
     */
    @Query("SELECT m FROM MemberGeneral m " +
           "LEFT JOIN FETCH m.construction " +
           "LEFT JOIN FETCH m.profilePhoto " +
           "LEFT JOIN FETCH m.laborContract " +
           "LEFT JOIN FETCH m.safetyEducationCert " +
           "LEFT JOIN FETCH m.protectiveGearPledge " +
           "LEFT JOIN FETCH m.etcDoc1 " +
           "LEFT JOIN FETCH m.etcDoc2 " +
           "LEFT JOIN FETCH m.etcDoc3 " +
           "WHERE m.construction.id = :constructionId")
    Optional<MemberGeneral> findByConstructionIdWithAttachments(@Param("constructionId") Long constructionId);

    // 날짜 검색을 위한 메서드들
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.createdAt >= :startDateTime")
    Page<MemberGeneral> findByConstructionIdAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("startDateTime") LocalDateTime startDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.createdAt <= :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    // 검색어와 날짜 조건을 모두 적용한 메서드들 (이미 있는 것들은 수정하지 않음)
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.username LIKE %:username%")
    Page<MemberGeneral> findByConstructionIdAndUsernameContaining(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.name LIKE %:name%")
    Page<MemberGeneral> findByConstructionIdAndNameContaining(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.email LIKE %:email%")
    Page<MemberGeneral> findByConstructionIdAndEmailContaining(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.phone LIKE %:phone%")
    Page<MemberGeneral> findByConstructionIdAndPhoneContaining(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            Pageable pageable);

    // 사용자명과 날짜 조건을 모두 적용한 검색 메서드
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.username LIKE %:username% AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndUsernameContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.username LIKE %:username% AND m.createdAt >= :startDateTime")
    Page<MemberGeneral> findByConstructionIdAndUsernameContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("startDateTime") LocalDateTime startDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.username LIKE %:username% AND m.createdAt <= :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndUsernameContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    // 이름과 날짜 조건을 모두 적용한 검색 메서드
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.name LIKE %:name% AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndNameContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.name LIKE %:name% AND m.createdAt >= :startDateTime")
    Page<MemberGeneral> findByConstructionIdAndNameContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            @Param("startDateTime") LocalDateTime startDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.name LIKE %:name% AND m.createdAt <= :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndNameContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    // 이메일과 날짜 조건을 모두 적용한 검색 메서드
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.email LIKE %:email% AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndEmailContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.email LIKE %:email% AND m.createdAt >= :startDateTime")
    Page<MemberGeneral> findByConstructionIdAndEmailContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            @Param("startDateTime") LocalDateTime startDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.email LIKE %:email% AND m.createdAt <= :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndEmailContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    // 전화번호와 날짜 조건을 모두 적용한 검색 메서드
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.phone LIKE %:phone% AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndPhoneContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.phone LIKE %:phone% AND m.createdAt >= :startDateTime")
    Page<MemberGeneral> findByConstructionIdAndPhoneContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            @Param("startDateTime") LocalDateTime startDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.phone LIKE %:phone% AND m.createdAt <= :endDateTime")
    Page<MemberGeneral> findByConstructionIdAndPhoneContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable);

    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.username = :username")
    Optional<MemberGeneral> findByUsernameWithConstruction(@Param("username") String username);

    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.id = :id")
    Optional<MemberGeneral> findByIdWithConstruction(@Param("id") Long id);

    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.username = :username and m.construction.id = :constructionId")
    Optional<MemberGeneral> findByUsernameAndConstructionIdWithConstruction(@Param("username") String username, @Param("constructionId") Long constructionId);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.username = :username")
    Optional<MemberGeneral> findByConstructionIdAndUsername(
            @Param("constructionId") Long constructionId,
            @Param("username") String username);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.email = :email")
    Optional<MemberGeneral> findByConstructionIdAndEmail(
            @Param("constructionId") Long constructionId,
            @Param("email") String email);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.phone = :phone")
    Optional<MemberGeneral> findByConstructionIdAndPhone(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.username = :username AND m.email = :email")
    Optional<MemberGeneral> findByConstructionIdAndUsernameAndEmail(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("email") String email);

    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.username = :username AND m.phone = :phone")
    Optional<MemberGeneral> findByConstructionIdAndUsernameAndPhone(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("phone") String phone);
}
