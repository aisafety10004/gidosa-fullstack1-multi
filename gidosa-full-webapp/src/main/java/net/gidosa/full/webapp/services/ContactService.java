package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.repositories.mysql.mybatis.ConstructionMyBatisRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ConstructionMyBatisRepository constructionRepository;

    public List<Construction> searchByKeyword(String keyword) {
        List<Construction> constructionList = constructionRepository.findByLocationContaining(keyword);
        return constructionList;
    }
}
