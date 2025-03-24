package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenuContentType5;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomMenuContentType5Repository extends JpaRepository<CustomMenuContentType5, Long> {
    
    // 특정 메뉴의 타입5 컨텐츠 조회
    Optional<CustomMenuContentType5> findByMenuId(Long menuId);
    
    // JPQL을 사용하여 메뉴와 첨부파일 정보를 함께 가져오는 메서드
    @Query("SELECT c FROM CustomMenuContentType5 c " +
           "LEFT JOIN FETCH c.menu " +
           "LEFT JOIN FETCH c.fileAttachment1 " +
           "LEFT JOIN FETCH c.fileAttachment2 " +
           "LEFT JOIN FETCH c.fileAttachment3 " +
           "WHERE c.menu.id = :menuId")
    Optional<CustomMenuContentType5> findByMenuIdWithAttachments(@Param("menuId") Long menuId);
    
    // ID로 컨텐츠와 첨부파일을 함께 조회하는 메서드
    @Query("SELECT c FROM CustomMenuContentType5 c " +
           "LEFT JOIN FETCH c.menu " +
           "LEFT JOIN FETCH c.fileAttachment1 " +
           "LEFT JOIN FETCH c.fileAttachment2 " +
           "LEFT JOIN FETCH c.fileAttachment3 " +
           "WHERE c.id = :contentId")
    Optional<CustomMenuContentType5> findByIdWithAttachments(@Param("contentId") Long contentId);
    
    // 특정 메뉴의 타입5 컨텐츠 삭제
    void deleteByMenuId(Long menuId);
} 