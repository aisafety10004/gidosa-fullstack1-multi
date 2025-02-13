package net.gidosa.rdb.entities;

import lombok.extern.log4j.Log4j2;
import net.gidosa.rdb.test_fixtures.supports.SpringBootTestSupport;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Log4j2
@Sql(
        scripts = {"classpath:dbs/schema-mysql-test.sql"},
        config = @SqlConfig(separator = "^;")
)
//@Sql(
//    scripts = {"classpath:dbs/schema-mysql-test.sql"}
//)
//@Sql
//@ContextConfiguration(initializers= {ConfigDataApplicationContextInitializer.class} )
public class JpaCommonEntityDDLTests extends SpringBootTestSupport {
    @Test
    @DisplayName("common에서 ddl 잘 만들어지는지 테스트")
    void jpaEntityDDLTest1() {
        log.info("common 엔티티 ddl 만들어짐");
    }
}