package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.repositories.mysql.jpa.ConstructionJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralConstructionService {
    private final ConstructionJpaRepository constructionJpaRepository;

    @Transactional(readOnly = true)
    public Construction getConstruction(Long constructionId) {
        return constructionJpaRepository.findById(constructionId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid construction Id:" + constructionId));
    }
}
