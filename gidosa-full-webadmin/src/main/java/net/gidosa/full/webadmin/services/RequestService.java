package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import net.gidosa.rdb.repositories.mysql.jpa.RequestConstructionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class RequestService {
    
    private final RequestConstructionJpaRepository requestConstructionRepository;
    
    @Transactional(readOnly = true)
    public Page<RequestConstruction> getRequestConstructions(Pageable pageable) {
        return requestConstructionRepository.findAllByOrderByIdDesc(pageable);
    }
} 