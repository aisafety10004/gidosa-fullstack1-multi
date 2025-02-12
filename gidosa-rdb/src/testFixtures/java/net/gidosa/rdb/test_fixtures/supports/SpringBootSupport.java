package net.gidosa.rdb.test_fixtures.supports;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        scanBasePackages = {
                "net.gidosa.rdb",
//                "net.gidosa.redis",
        }
)
public class SpringBootSupport {}
