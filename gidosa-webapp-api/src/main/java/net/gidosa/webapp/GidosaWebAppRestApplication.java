package net.gidosa.webapp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@Slf4j
//@EnableScheduling
//@EnableMongoAuditing
@SpringBootApplication(
        exclude = {
//            DataSourceAutoConfiguration.class
//            ElasticsearchDataAutoConfiguration.class,
//            ElasticsearchRestClientAutoConfiguration.class,
//            OpensearchDataAutoConfiguration.class,
//            OpensearchRestClientAutoConfiguration.class,
        },
        scanBasePackages = {
                "net.gidosa.webapp",
                "net.gidosa.common",
                "net.gidosa.common.api",
                "net.gidosa.auth",
                "net.gidosa.rdb",
//                "net.gidosa.redis",
//                "net.gidosa.queue_stream",
//                "net.gidosa.search",
//                "net.gidosa.dynamodb",
        }
)
//@ServletComponentScan
public class GidosaWebAppRestApplication implements ApplicationListener<ContextClosedEvent> {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GidosaWebAppRestApplication.class);
        application.addListeners(new ApplicationPidFileWriter("./gidosa-webapp-api.pid"));
        application.run(args);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 애플리케이션 종료 시 수행할 작업을 여기에 작성합니다.
        // 예: 리소스 정리, 로그 기록, 기타 등등
        log.info(GidosaWebAppRestApplication.class.getName() + "이 우아하게 종료되었습니다.");
    }
}