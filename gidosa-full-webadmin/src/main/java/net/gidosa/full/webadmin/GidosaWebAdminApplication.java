package net.gidosa.full.webadmin;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

@Log4j2
@SpringBootApplication(
        exclude = {
//            DataSourceAutoConfiguration.class
        },
        scanBasePackages = {
                "net.gidosa.full.webadmin",
                "net.gidosa.common",
                "net.gidosa.common.api",
                "net.gidosa.auth",
                "net.gidosa.rdb",
        }
)
public class GidosaWebAdminApplication implements ApplicationListener<ContextClosedEvent> {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GidosaWebAdminApplication.class);
        application.addListeners(new ApplicationPidFileWriter("./gidosa-full-webadmin.pid"));
        application.run(args);
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 애플리케이션 종료 시 수행할 작업을 여기에 작성합니다.
        // 예: 리소스 정리, 로그 기록, 기타 등등
        log.info(GidosaWebAdminApplication.class.getName() + "이 우아하게 종료되었습니다.");
    }

}
