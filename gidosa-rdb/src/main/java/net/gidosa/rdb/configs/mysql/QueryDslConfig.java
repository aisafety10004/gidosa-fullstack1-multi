package net.gidosa.rdb.configs.mysql;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import net.gidosa.rdb.constants.DbConsts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class QueryDslConfig {

//    @PersistenceContext(unitName = JpaEntityManager.RDS_MYSQL_ENTITY_MANAGER_UNIT_NAME, type = PersistenceContextType.EXTENDED)
    @PersistenceContext(unitName = DbConsts.RDS_MYSQL_ENTITY_MANAGER_UNIT_NAME)
    private final EntityManager entityManager;

    @Bean
//    @Qualifier(DatabaseConst.RDS_MYSQL_QUERY_DSL_FACTORY)
    public JPAQueryFactory rdsJpaQueryFactory(){
        return new JPAQueryFactory(this.entityManager);
    }
}
