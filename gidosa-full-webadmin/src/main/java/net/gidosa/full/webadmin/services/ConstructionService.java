package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
