package net.gidosa.common.api.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private static final String[] DEFAULT_EXCLUDE_PATH = {
            "/api/v3/",
    };
}
