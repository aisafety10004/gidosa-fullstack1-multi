package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class ConstructionService {
    private final ConstructionJpaRepository constructionJpaRepository;
    private final CustomMenuJpaRepository customMenuJpaRepository;

    public Page<Construction> findAllConstructions(Pageable pageable) {
        return constructionJpaRepository.findAllByOrderByIdDesc(pageable);
    }

    // 검색 기능을 위한 메소드 추가 - JPQL 쿼리 사용
    public Page<Construction> searchConstructions(String name, String location, String status, Pageable pageable) {
        // 빈 문자열이나 null인 경우 빈 문자열로 처리 (LIKE 검색에서 '%'로 처리됨)
        name = StringUtils.hasText(name) ? name : "";
        location = StringUtils.hasText(location) ? location : "";
        status = StringUtils.hasText(status) ? status : "";
        
        // 모든 필드가 빈 문자열인 경우 전체 목록 반환
        if (name.isEmpty() && location.isEmpty() && status.isEmpty()) {
            return constructionJpaRepository.findAllByOrderByIdDesc(pageable);
        }
        
        // 모든 검색 조건이 있는 경우
        if (!name.isEmpty() && !location.isEmpty() && !status.isEmpty()) {
            return constructionJpaRepository.findByNameContainingAndLocationContainingAndStatusContainingOrderByIdDesc(
                    name, location, status, pageable);
        }
        
        // 두 가지 검색 조건이 있는 경우
        if (!name.isEmpty() && !location.isEmpty()) {
            return constructionJpaRepository.findByNameContainingAndLocationContainingOrderByIdDesc(
                    name, location, pageable);
        }
        
        if (!name.isEmpty() && !status.isEmpty()) {
            return constructionJpaRepository.findByNameContainingAndStatusContainingOrderByIdDesc(
                    name, status, pageable);
        }
        
        if (!location.isEmpty() && !status.isEmpty()) {
            return constructionJpaRepository.findByLocationContainingAndStatusContainingOrderByIdDesc(
                    location, status, pageable);
        }
        
        // 한 가지 검색 조건만 있는 경우
        if (!name.isEmpty()) {
            return constructionJpaRepository.findByNameContainingOrderByIdDesc(name, pageable);
        }
        
        if (!location.isEmpty()) {
            return constructionJpaRepository.findByLocationContainingOrderByIdDesc(location, pageable);
        }
        
        if (!status.isEmpty()) {
            return constructionJpaRepository.findByStatusContainingOrderByIdDesc(status, pageable);
        }
        
        // 기본 반환 (위의 조건들로 모두 처리되어야 하지만, 안전을 위해 추가)
        return constructionJpaRepository.findAllByOrderByIdDesc(pageable);
    }
    
    // 정렬 및 페이지 크기 선택 기능이 추가된 검색 메소드
    public Page<Construction> searchConstructions(
            String name, String location, String status, 
            String sortField, Sort.Direction direction, Integer pageSize, int pageNumber) {
        
        // 빈 문자열이나 null인 경우 빈 문자열로 처리 (LIKE 검색에서 '%'로 처리됨)
        name = StringUtils.hasText(name) ? name : "";
        location = StringUtils.hasText(location) ? location : "";
        status = StringUtils.hasText(status) ? status : "";
        
        // 정렬 필드가 없는 경우 기본값 설정
        if (!StringUtils.hasText(sortField)) {
            sortField = "id";
        }
        
        // 페이지 크기가 없는 경우 기본값 설정
        if (pageSize == null || pageSize <= 0) {
            pageSize = 10;
        }
        
        // 정렬 객체 생성
        Sort sort = Sort.by(direction, sortField);
        
        // 페이지 요청 객체 생성
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        
        // 모든 필드가 빈 문자열인 경우 전체 목록 반환
        if (name.isEmpty() && location.isEmpty() && status.isEmpty()) {
            return constructionJpaRepository.findAll(pageable);
        }
        
        // 모든 검색 조건이 있는 경우
        if (!name.isEmpty() && !location.isEmpty() && !status.isEmpty()) {
            return constructionJpaRepository.findByNameContainingAndLocationContainingAndStatusContaining(
                    name, location, status, pageable);
        }
        
        // 두 가지 검색 조건이 있는 경우
        if (!name.isEmpty() && !location.isEmpty()) {
            return constructionJpaRepository.findByNameContainingAndLocationContaining(
                    name, location, pageable);
        }
        
        if (!name.isEmpty() && !status.isEmpty()) {
            return constructionJpaRepository.findByNameContainingAndStatusContaining(
                    name, status, pageable);
        }
        
        if (!location.isEmpty() && !status.isEmpty()) {
            return constructionJpaRepository.findByLocationContainingAndStatusContaining(
                    location, status, pageable);
        }
        
        // 한 가지 검색 조건만 있는 경우
        if (!name.isEmpty()) {
            return constructionJpaRepository.findByNameContaining(name, pageable);
        }
        
        if (!location.isEmpty()) {
            return constructionJpaRepository.findByLocationContaining(location, pageable);
        }
        
        if (!status.isEmpty()) {
            return constructionJpaRepository.findByStatusContaining(status, pageable);
        }
        
        // 기본 반환 (위의 조건들로 모두 처리되어야 하지만, 안전을 위해 추가)
        return constructionJpaRepository.findAll(pageable);
    }

    public Construction getConstructionWithManagementMenus(Long id) {
        Construction construction = constructionJpaRepository.findByIdWithManagementMenus(id);
        return construction;
    }
    
    /**
     * 모든 건설현장 정보를 관리 메뉴와 함께 가져옵니다.
     * Thymeleaf 뷰에서 사용하기 위한 메소드입니다.
     * @return 관리 메뉴가 포함된 모든 건설현장 목록
     */
    @Transactional(readOnly = true)
    public List<Construction> findAllConstructionsWithManagementMenus() {
        List<Construction> constructions = constructionJpaRepository.findAll();
        // 각 건설현장에 대해 관리 메뉴 정보를 로드
        for (Construction construction : constructions) {
            if (construction.getId() != null) {
                Construction withMenus = constructionJpaRepository.findByIdWithManagementMenus(construction.getId());
                if (withMenus != null && withMenus.getManagementMenus() != null) {
                    construction.setManagementMenus(withMenus.getManagementMenus());
                }
            }
        }
        return constructions;
    }

    public Construction saveConstruction(Construction construction) {
        if (construction.getId() != null) {
            // If updating existing construction, preserve createdAt
            Construction existingConstruction = constructionJpaRepository.findById(construction.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid construction Id: " + construction.getId()));
            construction.setCreatedAt(existingConstruction.getCreatedAt());
        }
        return constructionJpaRepository.save(construction);
    }

    public void deleteConstruction(Long id) {
        constructionJpaRepository.deleteById(id);
    }
    
    /**
     * 건설 현장과 관련된 커스텀 메뉴를 함께 삭제합니다.
     */
    @Transactional
    public void deleteConstructionWithCustomMenus(Long constructionId) {
        // 관련 커스텀 메뉴 먼저 삭제
        List<CustomMenu> customMenus = customMenuJpaRepository.findByConstructionIdOrderByDisplayOrderAsc(constructionId);
        for (CustomMenu menu : customMenus) {
            customMenuJpaRepository.deleteById(menu.getId());
        }
        
        // 건설 현장 삭제
        constructionJpaRepository.deleteById(constructionId);
    }
    
    /**
     * 템플릿 커스텀 메뉴를 건설 현장에 복사합니다.
     * 같은 URL을 가진 메뉴가 이미 존재하는 경우 복사하지 않습니다.
     */
    @Transactional
    public void copyCustomMenusToConstruction(Long constructionId, List<Long> templateMenuIds) {
        if (templateMenuIds == null || templateMenuIds.isEmpty()) {
            return;
        }
        
        Construction construction = constructionJpaRepository.findById(constructionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid construction Id: " + constructionId));
        
        // 현재 건설 현장의 커스텀 메뉴를 조회하여 URL 목록 추출
        List<CustomMenu> existingMenus = customMenuJpaRepository.findByConstructionIdOrderByDisplayOrderAsc(constructionId);
        List<String> existingUrls = existingMenus.stream()
                .map(CustomMenu::getUrl)
                .collect(Collectors.toList());
        
        for (Long templateMenuId : templateMenuIds) {
            CustomMenu templateMenu = customMenuJpaRepository.findByIdWithConstruction(templateMenuId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid template menu Id: " + templateMenuId));
            
            // 템플릿 메뉴가 아니면 건너뜀
            if (templateMenu.getConstruction() != null) {
                continue;
            }
            
            // 같은 URL을 가진 메뉴가 이미 존재하면 건너뜀
            if (existingUrls.contains(templateMenu.getUrl())) {
                continue;
            }
            
            // 템플릿 메뉴를 복사하여 건설 현장에 연결
            copyMenuAndChildren(templateMenu, construction, null);
        }
    }
    
    /**
     * 메뉴와 하위 메뉴를 재귀적으로 복사합니다.
     */
    private CustomMenu copyMenuAndChildren(CustomMenu sourceMenu, Construction construction, CustomMenu newParent) {
        // 메뉴 복사
        CustomMenu newMenu = new CustomMenu();
        newMenu.setName(sourceMenu.getName());
        newMenu.setUrl(sourceMenu.getUrl());
        newMenu.setDescription(sourceMenu.getDescription());
        newMenu.setMenuType(sourceMenu.getMenuType());
        newMenu.setDisplayOrder(sourceMenu.getDisplayOrder());
        newMenu.setIsActive(sourceMenu.getIsActive());
        newMenu.setConstruction(construction);
        newMenu.setParent(newParent);
        
        // 새 메뉴 저장
        CustomMenu savedMenu = customMenuJpaRepository.save(newMenu);
        
        // 하위 메뉴가 있으면 재귀적으로 복사
        if (sourceMenu.getChildren() != null && !sourceMenu.getChildren().isEmpty()) {
            for (CustomMenu child : sourceMenu.getChildren()) {
                copyMenuAndChildren(child, construction, savedMenu);
            }
        }
        
        return savedMenu;
    }
    
    /**
     * 건설 현장의 선택된 커스텀 메뉴 ID 목록을 조회합니다.
     * (URL을 기준으로 템플릿 메뉴와 비교하여, 동일한 URL을 가진 메뉴가 있다면 선택된 것으로 간주)
     */
    @Transactional(readOnly = true)
    public List<Long> getSelectedCustomMenuIds(Long constructionId) {
        Construction construction = constructionJpaRepository.findById(constructionId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid construction Id: " + constructionId));
        
        // 현재 건설 현장의 커스텀 메뉴를 조회
        List<CustomMenu> customMenus = customMenuJpaRepository.findByConstructionIdOrderByDisplayOrderAsc(constructionId);
        
        // 템플릿 메뉴 목록 조회
        List<CustomMenu> templateMenus = customMenuJpaRepository.findTemplateMenus();
        
        List<Long> selectedIds = new ArrayList<>();
        for (CustomMenu template : templateMenus) {
            // URL이 일치하는 메뉴가 있으면 선택된 것으로 간주
            boolean isSelected = customMenus.stream()
                    .anyMatch(menu -> menu.getUrl().equals(template.getUrl()));
            
            if (isSelected) {
                selectedIds.add(template.getId());
            }
        }
        
        return selectedIds;
    }
    
    /**
     * 건설 현장의 커스텀 메뉴를 업데이트합니다.
     */
    @Transactional
    public void updateCustomMenusForConstruction(Long constructionId, List<Long> newSelectedMenuIds) {
        // 템플릿 메뉴 목록 조회 (admin이 만든 커스텀 메뉴)
        List<CustomMenu> templateMenus = customMenuJpaRepository.findTemplateMenus();
        List<String> templateUrls = templateMenus.stream()
                .map(CustomMenu::getUrl)
                .collect(Collectors.toList());
        
        // 현재 건설 현장의 커스텀 메뉴 조회
        List<CustomMenu> existingMenus = customMenuJpaRepository.findByConstructionIdOrderByDisplayOrderAsc(constructionId);
        
        // admin이 만든 커스텀 메뉴만 삭제 (URL을 기준으로 판단)
        for (CustomMenu menu : existingMenus) {
            if (templateUrls.contains(menu.getUrl())) {
                customMenuJpaRepository.deleteById(menu.getId());
            }
        }
        
        // 새로 선택된 메뉴 복사
        if (newSelectedMenuIds != null && !newSelectedMenuIds.isEmpty()) {
            copyCustomMenusToConstruction(constructionId, newSelectedMenuIds);
        }
    }
}
