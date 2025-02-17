package net.gidosa.full.webapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webapp.models.dtos.RequestConstructionDto;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.RequestConstruction;
import net.gidosa.rdb.repositories.mysql.jpa.RequestConstructionJpaRepository;
import net.gidosa.rdb.repositories.mysql.mybatis.ConstructionMyBatisRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContactService {
    private final ConstructionMyBatisRepository constructionRepository;
    private final RequestConstructionJpaRepository requestConstructionRepository;

    public List<Construction> searchByKeyword(String keyword) {
        List<Construction> constructionList = constructionRepository.findByLocationContaining(keyword);
        return constructionList;
    }

    @Transactional
    public RequestConstruction submitRequest(RequestConstructionDto request) {
        RequestConstruction requestConstruction = RequestConstruction.builder()
            .name(request.name())
            .phone(request.phone())
            .constructionLocation(request.constructionLocation())
            .position(request.position())
            .message(request.message())
            .agreement(request.agreement())
            .build();

        return requestConstructionRepository.save(requestConstruction);
    }
}
