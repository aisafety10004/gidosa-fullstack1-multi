//package net.gidosa.full.webapp.configs;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
//
//@Configuration
//public class ThymeleafJsonConfig {
//
//    @Bean
//    public ClassLoaderTemplateResolver jsonTemplateResolver() {
//        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
//        resolver.setPrefix("templates/"); // src/main/resources/templates/
//        resolver.setSuffix(".json");
//        resolver.setTemplateMode("TEXT"); // JSON은 HTML이 아님
//        resolver.setCharacterEncoding("UTF-8");
//        resolver.setOrder(2); // 기존 HTML 리졸버보다 나중에 적용
//        resolver.setCheckExistence(true); // 템플릿 없을 경우 예외 방지
//        return resolver;
//    }
//}