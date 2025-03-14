package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
//@Transactional(readOnly = true)
public class ConstructionService {
    private final ConstructionJpaRepository constructionJpaRepository;

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
}
