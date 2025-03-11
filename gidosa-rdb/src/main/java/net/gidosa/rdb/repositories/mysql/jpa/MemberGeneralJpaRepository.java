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

    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.username = :username")
    Optional<MemberGeneral> findByUsernameWithConstruction(@Param("username") String username);
    @Query("SELECT m FROM MemberGeneral m LEFT JOIN FETCH m.construction WHERE m.id = :id")
    Optional<MemberGeneral> findByIdWithConstruction(@Param("id") Long id);

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
    
    // 검색 기능을 위한 메소드 추가
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND (:username IS NULL OR LOWER(m.username) LIKE LOWER(CONCAT('%', :username, '%')))")
    Page<MemberGeneral> findByConstructionIdAndUsernameContaining(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            Pageable pageable);
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND (:name IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<MemberGeneral> findByConstructionIdAndNameContaining(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            Pageable pageable);
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND (:email IS NULL OR LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<MemberGeneral> findByConstructionIdAndEmailContaining(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            Pageable pageable);
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND (:phone IS NULL OR LOWER(m.phone) LIKE LOWER(CONCAT('%', :phone, '%')))")
    Page<MemberGeneral> findByConstructionIdAndPhoneContaining(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            Pageable pageable);
    
    // 날짜 검색을 위한 메소드
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.createdAt BETWEEN :startDate AND :endDate")
    Page<MemberGeneral> findByConstructionIdAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.createdAt >= :startDate")
    Page<MemberGeneral> findByConstructionIdAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("startDate") java.time.LocalDateTime startDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND m.createdAt <= :endDate")
    Page<MemberGeneral> findByConstructionIdAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
    
    // 검색 조건과 날짜를 함께 검색하는 메소드 - 사용자명
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.username) LIKE LOWER(CONCAT('%', :username, '%')) AND m.createdAt BETWEEN :startDate AND :endDate")
    Page<MemberGeneral> findByConstructionIdAndUsernameContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.username) LIKE LOWER(CONCAT('%', :username, '%')) AND m.createdAt >= :startDate")
    Page<MemberGeneral> findByConstructionIdAndUsernameContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("startDate") java.time.LocalDateTime startDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.username) LIKE LOWER(CONCAT('%', :username, '%')) AND m.createdAt <= :endDate")
    Page<MemberGeneral> findByConstructionIdAndUsernameContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("username") String username,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
    
    // 검색 조건과 날짜를 함께 검색하는 메소드 - 이름
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.createdAt BETWEEN :startDate AND :endDate")
    Page<MemberGeneral> findByConstructionIdAndNameContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.createdAt >= :startDate")
    Page<MemberGeneral> findByConstructionIdAndNameContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            @Param("startDate") java.time.LocalDateTime startDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.name) LIKE LOWER(CONCAT('%', :name, '%')) AND m.createdAt <= :endDate")
    Page<MemberGeneral> findByConstructionIdAndNameContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("name") String name,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
    
    // 검색 조건과 날짜를 함께 검색하는 메소드 - 이메일
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%')) AND m.createdAt BETWEEN :startDate AND :endDate")
    Page<MemberGeneral> findByConstructionIdAndEmailContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%')) AND m.createdAt >= :startDate")
    Page<MemberGeneral> findByConstructionIdAndEmailContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            @Param("startDate") java.time.LocalDateTime startDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.email) LIKE LOWER(CONCAT('%', :email, '%')) AND m.createdAt <= :endDate")
    Page<MemberGeneral> findByConstructionIdAndEmailContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("email") String email,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
    
    // 검색 조건과 날짜를 함께 검색하는 메소드 - 전화번호
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.phone) LIKE LOWER(CONCAT('%', :phone, '%')) AND m.createdAt BETWEEN :startDate AND :endDate")
    Page<MemberGeneral> findByConstructionIdAndPhoneContainingAndCreatedAtBetween(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            @Param("startDate") java.time.LocalDateTime startDate,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.phone) LIKE LOWER(CONCAT('%', :phone, '%')) AND m.createdAt >= :startDate")
    Page<MemberGeneral> findByConstructionIdAndPhoneContainingAndCreatedAtGreaterThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            @Param("startDate") java.time.LocalDateTime startDate,
            Pageable pageable);
            
    @Query("SELECT m FROM MemberGeneral m WHERE m.construction.id = :constructionId AND LOWER(m.phone) LIKE LOWER(CONCAT('%', :phone, '%')) AND m.createdAt <= :endDate")
    Page<MemberGeneral> findByConstructionIdAndPhoneContainingAndCreatedAtLessThanEqual(
            @Param("constructionId") Long constructionId,
            @Param("phone") String phone,
            @Param("endDate") java.time.LocalDateTime endDate,
            Pageable pageable);
}
