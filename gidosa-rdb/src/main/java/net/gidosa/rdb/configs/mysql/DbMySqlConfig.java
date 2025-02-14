package net.gidosa.rdb.configs.mysql;

import lombok.RequiredArgsConstructor;
import net.gidosa.rdb.constants.DbConsts;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
//@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
//@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {
        "net.gidosa.rdb.repositories.mysql.jpa",
})
public class DbMySqlConfig {
    private final DataSource mysqlJpaMaster1DataSource;
    private final JpaProperties jpaProperties;

    @Bean
    JpaTransactionManager transactionManager() {
        return new JpaTransactionManager();
    }

    @Bean
//    @DependsOn({"mysqlJpaMaster1DataSource"})
//    @DependsOn({"mysqlJpaMaster1DataSource", "mysqlJpaSlave1DataSource"})
    DataSource jpaDataSource() {
        return mysqlJpaMaster1DataSource;
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(@Qualifier("jpaDataSource") DataSource mysqlJpaDataSource) {
        AbstractJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        EntityManagerFactoryBuilder entityManagerFactoryBuilder = new EntityManagerFactoryBuilder(vendorAdapter, jpaProperties.getProperties(), null);
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = entityManagerFactoryBuilder.dataSource(mysqlJpaDataSource)
                .packages(new String[] {
                        "net.gidosa.rdb.models.entities.dbs.mysql",
                })
                .build();
        localContainerEntityManagerFactoryBean.setPersistenceUnitName(DbConsts.RDS_MYSQL_ENTITY_MANAGER_UNIT_NAME);

        return localContainerEntityManagerFactoryBean;
    }
}
