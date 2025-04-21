package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomMenu;
import net.gidosa.rdb.repositories.mysql.jpa.CustomMenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralCustomMenuService {
    private final CustomMenuRepository customMenuRepository;

    /**
     * 특정 건설 현장의 모든 메뉴를 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getAllMenusByConstructionId(Long constructionId) {
        return customMenuRepository.findByConstructionIdOrderByDisplayOrderAsc(constructionId);
    }

    /**
     * 특정 건설 현장의 루트 메뉴와 하위 메뉴를 함께 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CustomMenu> getRootMenusWithChildrenByConstructionId(Long constructionId) {
        return customMenuRepository.findRootMenusWithChildrenByConstructionId(constructionId);
    }
} 