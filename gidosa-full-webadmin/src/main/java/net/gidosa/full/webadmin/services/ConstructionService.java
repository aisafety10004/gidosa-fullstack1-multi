package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (construction == null) {
            throw new IllegalArgumentException("Invalid construction Id: " + id);
        }
        return construction;
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
