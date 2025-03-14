package net.gidosa.full.webadmin.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import net.gidosa.rdb.repositories.mysql.jpa.RequestConstructionJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Log4j2
@Service
@RequiredArgsConstructor
public class RequestService {
    
    private final RequestConstructionJpaRepository requestConstructionRepository;
    
    @Transactional(readOnly = true)
    public Page<RequestConstruction> getRequestConstructions(Pageable pageable) {
        return requestConstructionRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<RequestConstruction> searchRequestConstructions(String searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return getRequestConstructions(pageable);
        }

        // 정렬은 pageable에서 처리하므로 기존 메서드를 사용
        // 기존 메서드는 ORDER BY r.id DESC가 포함되어 있지만, pageable의 정렬이 우선 적용됨
        switch (searchType) {
            case "name":
                return requestConstructionRepository.findByNameContaining(searchKeyword, pageable);
            case "phone":
                return requestConstructionRepository.findByPhoneContaining(searchKeyword, pageable);
            case "location":
                return requestConstructionRepository.findByConstructionLocationContaining(searchKeyword, pageable);
            default:
                return getRequestConstructions(pageable);
        }
    }
} 