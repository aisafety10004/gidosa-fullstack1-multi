package net.gidosa.rdb.repositories.mysql.querydsl;

import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class ConstructionCustomQueryDslRepository extends QuerydslRepositorySupport {
    public ConstructionCustomQueryDslRepository() {
        super(Construction.class);
    }
    public ConstructionCustomQueryDslRepository(Class<?> domainClass) {
        super(domainClass);
    }
}
