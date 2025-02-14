package net.gidosa.rdb.configs.mysql.master;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

// https://colour-my-memories-blue.tistory.com/15 참고1
// https://bugoverdose.github.io/docs/database-connection-pool-sizing 참고2
@Configuration
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
//@RequiredArgsConstructor
//@NoArgsConstructor
public class MysqlMaster1Config {
    private final String driverClassName;
    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final Integer maxPoolSize;

    public MysqlMaster1Config(@Value("${spring.datasource.mysql.master1.driver-class-name}") String driverClassName
            , @Value("${spring.datasource.mysql.master1.jdbc-url}") String jdbcUrl
            , @Value("${spring.datasource.mysql.master1.max-pool-size}") int maxPoolSize
            , @Value("${spring.datasource.mysql.master1.username}") String username
            , @Value("${spring.datasource.mysql.master1.password}") String password) {
        this.driverClassName = driverClassName;
        this.jdbcUrl = jdbcUrl;
        this.maxPoolSize = maxPoolSize;
        this.username = username;
        this.password = password;
    }


    //    @Primary
    @Bean
    public DataSource mysqlJpaMaster1DataSource() {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setPoolName("mysql-master1-jpa");
        hikariDataSource.setDriverClassName(this.driverClassName);
        hikariDataSource.setUsername(this.username);
        hikariDataSource.setJdbcUrl(this.jdbcUrl);
        hikariDataSource.setPassword(this.password);
        hikariDataSource.setReadOnly(false);
        hikariDataSource.setMaximumPoolSize(maxPoolSize);
        hikariDataSource.setMaxLifetime(355000);                // 단위 ms
        hikariDataSource.setConnectionTimeout(355000);

        hikariDataSource.setKeepaliveTime(30000);
        hikariDataSource.addDataSourceProperty("cachePrepStmts", "true");
        hikariDataSource.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariDataSource.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return hikariDataSource;
    }
}
