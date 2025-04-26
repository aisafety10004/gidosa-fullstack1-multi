package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.CustomHtmlPage;
import net.gidosa.rdb.repositories.mysql.jpa.CustomHtmlPageJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class GeneralCustomHtmlService {
    private final CustomHtmlPageJpaRepository customHtmlPageJpaRepository;

    @Transactional(readOnly = true)
    public List<CustomHtmlPage> getCustomHtmlMainPagesByConstructionId(boolean isMainPage, Long constructionId) {
        return customHtmlPageJpaRepository.findByIsMainPageAndConstructionId(isMainPage, constructionId);
    }
}
