package net.gidosa.rdb.repositories.mysql.jpa;

import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomMenuJpaRepository extends JpaRepository<CustomMenu, Long> {
    
    // ID로 메뉴 조회 (Construction 포함, 없는 경우도 처리)
    @Query("SELECT m FROM CustomMenu m LEFT JOIN FETCH m.construction WHERE m.id = :id")
    Optional<CustomMenu> findByIdWithConstruction(@Param("id") Long id);
    
    // 특정 건설 현장의 모든 메뉴 조회 (상위 메뉴만)
    @Query("SELECT m FROM CustomMenu m JOIN FETCH m.construction WHERE m.construction.id = :constructionId AND m.parent IS NULL ORDER BY m.displayOrder ASC")
    List<CustomMenu> findRootMenusByConstructionId(@Param("constructionId") Long constructionId);
    
    // 특정 건설 현장의 모든 메뉴 조회 (계층 구조 포함)
    @Query("SELECT DISTINCT m FROM CustomMenu m JOIN FETCH m.construction LEFT JOIN FETCH m.children c LEFT JOIN FETCH c.construction WHERE m.construction.id = :constructionId AND m.parent IS NULL ORDER BY m.displayOrder ASC")
    List<CustomMenu> findRootMenusWithChildrenByConstructionId(@Param("constructionId") Long constructionId);
    
    // 모든 루트 메뉴 조회 (관리자용)
    @Query("SELECT m FROM CustomMenu m LEFT JOIN FETCH m.construction WHERE m.parent IS NULL AND m.construction IS NULL ORDER BY m.construction.id, m.displayOrder ASC")
    List<CustomMenu> findAllRootMenus();
    
    // 모든 루트 메뉴와 하위 메뉴 조회 (관리자용)
//    @Query("SELECT DISTINCT m FROM CustomMenu m JOIN FETCH m.construction LEFT JOIN FETCH m.children c LEFT JOIN FETCH c.construction WHERE m.parent IS NULL ORDER BY m.construction.id, m.displayOrder ASC")
    @Query("SELECT m FROM CustomMenu m LEFT JOIN FETCH m.construction LEFT JOIN FETCH m.children c LEFT JOIN FETCH c.construction WHERE m.parent IS NULL ORDER BY COALESCE(m.construction.id, 0), m.displayOrder ASC")
    List<CustomMenu> findAllRootMenusWithChildren();

    // Contruction이 없는 모든 루트 메뉴와 하위 메뉴 조회 (관리자용)
    @Query("SELECT m FROM CustomMenu m LEFT JOIN FETCH m.construction LEFT JOIN FETCH m.children c LEFT JOIN FETCH c.construction WHERE m.parent IS NULL AND m.construction IS NULL ORDER BY m.displayOrder ASC")
    List<CustomMenu> findAllRootMenusAdminWithChildren();
    
    // 특정 상위 메뉴의 하위 메뉴 조회
    @Query("SELECT m FROM CustomMenu m JOIN FETCH m.construction WHERE m.parent.id = :parentId ORDER BY m.displayOrder ASC")
    List<CustomMenu> findByParentIdOrderByDisplayOrderAsc(@Param("parentId") Long parentId);
    
    // 특정 건설 현장의 모든 메뉴 조회 (평면 구조)
    @Query("SELECT m FROM CustomMenu m JOIN FETCH m.construction WHERE m.construction.id = :constructionId ORDER BY m.displayOrder ASC")
    List<CustomMenu> findByConstructionIdOrderByDisplayOrderAsc(@Param("constructionId") Long constructionId);
    
    // 특정 메뉴 타입의 메뉴 조회
    @Query("SELECT m FROM CustomMenu m JOIN FETCH m.construction WHERE m.construction.id = :constructionId AND m.menuType = :menuType ORDER BY m.displayOrder ASC")
    List<CustomMenu> findByConstructionIdAndMenuTypeOrderByDisplayOrderAsc(@Param("constructionId") Long constructionId, @Param("menuType") Integer menuType);
    
    // URL로 메뉴 조회
    @Query("SELECT m FROM CustomMenu m JOIN FETCH m.construction WHERE m.construction.id = :constructionId AND m.url = :url")
    CustomMenu findByConstructionIdAndUrl(@Param("constructionId") Long constructionId, @Param("url") String url);

    //// URL 패턴으로 메뉴 조회 (URL이 특정 패턴으로 시작하는 메뉴 조회)
    // URL로 메뉴 조회 (패턴 매칭)
    @Query("SELECT m FROM CustomMenu m JOIN FETCH m.construction WHERE m.construction.id = :constructionId AND :url LIKE CONCAT(m.url, '%') ORDER BY LENGTH(m.url) DESC")
    List<CustomMenu> findByConstructionIdAndUrlPattern(@Param("constructionId") Long constructionId, @Param("url") String url);
    
    // 템플릿 메뉴 조회 (construction이 null인 메뉴들)
    @Query("SELECT m FROM CustomMenu m LEFT JOIN FETCH m.children c WHERE m.construction IS NULL ORDER BY m.displayOrder ASC")
    List<CustomMenu> findTemplateMenus();

    // URL로 메뉴 조회 (construction이 null인 메뉴들)
    @Query("SELECT m FROM CustomMenu m LEFT JOIN FETCH m.construction WHERE m.construction IS NULL AND m.url = :url")
    CustomMenu findByConstructionIsNullAndUrl(@Param("url") String url);
} 