package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.models.dtos.CustomMenuDto;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuJpaRepository;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CustomMenuService {
    
    private final CustomMenuJpaRepository customMenuJpaRepository;
    private final ConstructionJpaRepository constructionJpaRepository;
    
    /**
     * 특정 건설 현장의 모든 메뉴를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getAllMenusByConstructionId(Long constructionId) {
        return customMenuJpaRepository.findByConstructionIdOrderByDisplayOrderAsc(constructionId);
    }
    
    /**
     * 특정 건설 현장의 루트 메뉴를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getRootMenusByConstructionId(Long constructionId) {
        return customMenuJpaRepository.findRootMenusByConstructionId(constructionId);
    }
    
    /**
     * 특정 건설 현장의 루트 메뉴와 하위 메뉴를 함께 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getRootMenusWithChildrenByConstructionId(Long constructionId) {
        return customMenuJpaRepository.findRootMenusWithChildrenByConstructionId(constructionId);
    }
    
    /**
     * 특정 상위 메뉴의 하위 메뉴를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getChildMenusByParentId(Long parentId) {
        return customMenuJpaRepository.findByParentIdOrderByDisplayOrderAsc(parentId);
    }
    
    /**
     * 메뉴를 저장합니다.
     */
    @Transactional
    public CustomMenu saveMenu(CustomMenuDto menuDto) {
        CustomMenu menu = convertToEntity(menuDto);
        return customMenuJpaRepository.save(menu);
    }
    
    /**
     * 메뉴를 삭제합니다.
     */
    @Transactional
    public void deleteMenu(Long menuId) {
        customMenuJpaRepository.deleteById(menuId);
    }
    
    /**
     * 메뉴를 조회합니다.
     */
    @Transactional(readOnly = true)
    public Optional<CustomMenu> getMenuById(Long menuId) {
        return customMenuJpaRepository.findByIdWithConstruction(menuId);
    }
    
    /**
     * 특정 메뉴 타입의 메뉴를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getMenusByTypeAndConstructionId(Long constructionId, Integer menuType) {
    // public CustomMenu getMenusByUrl(Long constructionId, String url) {
        return customMenuJpaRepository.findByConstructionIdAndMenuTypeOrderByDisplayOrderAsc(constructionId, menuType);
        // log.info("Searching for menu with constructionId: {} and URL: {}", constructionId, url);
        
        // // 1. 정확한 URL 매칭 시도
        // CustomMenu menu = customMenuRepository.findByConstructionIdAndUrl(constructionId, url);
        
        // // 2. 정확한 매칭이 없으면 패턴 매칭 시도
        // if (menu == null) {
        //     log.info("No exact URL match found, trying pattern matching");
        //     List<CustomMenu> allMenus = customMenuRepository.findByConstructionIdOrderByDisplayOrderAsc(constructionId);
            
        //     // URL 패턴 매칭 (가장 긴 매칭을 우선)
        //     int maxMatchLength = 0;
        //     CustomMenu bestMatch = null;
            
        //     for (CustomMenu m : allMenus) {
        //         String menuUrl = m.getUrl();
        //         if (url.startsWith(menuUrl) && menuUrl.length() > maxMatchLength) {
        //             maxMatchLength = menuUrl.length();
        //             bestMatch = m;
        //         }
        //     }
            
        //     if (bestMatch != null) {
        //         log.info("Found best match by pattern: {} with URL: {}", bestMatch.getName(), bestMatch.getUrl());
        //         menu = bestMatch;
        //     }
        // }
        // if (menu == null) {
        //     // URL 패턴 매칭 (가장 긴 URL 패턴을 우선 - Repository 쿼리에서 정렬됨)
        //     List<CustomMenu> patternMatches = customMenuRepository.findByConstructionIdAndUrlPattern(constructionId, url);
            
        //     if (!patternMatches.isEmpty()) {
        //         menu = patternMatches.get(0); // 가장 긴 매칭을 선택 (첫 번째 결과)
        //         log.info("Found pattern match: {} with URL: {}", menu.getName(), menu.getUrl());
        //     }
        // }
        
        // return menu;
    }
    
    /**
     * URL로 메뉴를 조회합니다.
     */
    @Transactional(readOnly = true)
    public CustomMenu getMenuByUrl(Long constructionId, String url) {
        return customMenuJpaRepository.findByConstructionIdAndUrl(constructionId, url);
    }

    /**
     * URL로 메뉴를 조회합니다.(With Contruction이 없는 메뉴)
     */
    @Transactional(readOnly = true)
    public CustomMenu getMenuByConstructionIsNullAndUrl(String url) {
        return customMenuJpaRepository.findByConstructionIsNullAndUrl(url);
    }
    
    /**
     * DTO를 엔티티로 변환합니다.
     */
    private CustomMenu convertToEntity(CustomMenuDto dto) {
        CustomMenu menu = new CustomMenu();
        
        if (dto.getId() != null) {
            menu = customMenuJpaRepository.findByIdWithConstruction(dto.getId())
                    .orElse(new CustomMenu());
        }
        
        menu.setName(dto.getName());
        menu.setUrl(dto.getUrl());
        menu.setDescription(dto.getDescription());
        menu.setMenuType(dto.getMenuType());
        menu.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : 0);
        menu.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        // 건설 현장 설정
        if(dto.getConstructionId() != null) {
            Construction construction = constructionJpaRepository.findById(dto.getConstructionId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid construction Id: " + dto.getConstructionId()));
            menu.setConstruction(construction);
        } else {
            menu.setConstruction(null);
        }
        
        // 상위 메뉴 설정
        if (dto.getParentId() != null) {
            CustomMenu parent = customMenuJpaRepository.findByIdWithConstruction(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid parent menu Id: " + dto.getParentId()));
            menu.setParent(parent);
        } else {
            menu.setParent(null);
        }
        
        return menu;
    }
    
    /**
     * 엔티티를 DTO로 변환합니다.
     */
    public CustomMenuDto convertToDto(CustomMenu menu) {
        return CustomMenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .url(menu.getUrl())
                .description(menu.getDescription())
                .menuType(menu.getMenuType())
                .parentId(menu.getParent() != null ? menu.getParent().getId() : null)
                .displayOrder(menu.getDisplayOrder())
                .isActive(menu.getIsActive())
                .constructionId(Objects.isNull(menu.getConstruction()) ? null : menu.getConstruction().getId())
                .build();
    }
    
    /**
     * 모든 루트 메뉴를 조회합니다. (관리자용)
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getAllRootMenus() {
        return customMenuJpaRepository.findAllRootMenus();
    }
    
    /**
     * 모든 루트 메뉴와 하위 메뉴를 함께 조회합니다. (관리자용)
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getAllRootMenusWithChildren() {
        return customMenuJpaRepository.findAllRootMenusWithChildren();
    }

    /**
     * Contruction이 없는 루트 메뉴와 하위 메뉴를 함께 조회합니다. (관리자용)
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getAllRootMenusAdminWithChildren() {
        return customMenuJpaRepository.findAllRootMenusAdminWithChildren();
    }
    
    /**
     * 모든 건설 현장을 조회합니다. (관리자용)
     */
    @Transactional(readOnly = true)
    public List<Construction> getAllConstructions() {
        return constructionJpaRepository.findAll();
    }
    
    /**
     * 템플릿 커스텀 메뉴를 조회합니다. (construction_id가 null인 메뉴)
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getTemplateCustomMenus() {
        return customMenuJpaRepository.findTemplateMenus();
    }
} 