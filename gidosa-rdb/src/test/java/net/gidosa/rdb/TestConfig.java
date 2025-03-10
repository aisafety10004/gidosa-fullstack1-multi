package net.gidosa.rdb;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = "net.gidosa.rdb.models.entities.dbs.mysql")
@EnableJpaRepositories(basePackages = "net.gidosa.rdb.repositories.mysql.jpa")
public class TestConfig {
    // 테스트 구성을 위한 빈 클래스
} 