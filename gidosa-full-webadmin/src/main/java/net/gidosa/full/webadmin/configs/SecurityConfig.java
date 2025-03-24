package net.gidosa.full.webadmin.configs;

import lombok.RequiredArgsConstructor;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetailsService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final DataSource mysqlJpaMaster1DataSource;                                // MariaDB와 JAVA의 연결소스(고리)
    private final PrincipalDetailsService userDetailsService;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addAllowedOriginPattern("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .csrf(csrf -> csrf.disable())  // CSRF 보호는 필요에 따라 활성화하세요
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/assets/**", "/vendor/**", "/error/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/member/admin/**", "/construction/**", "/request/construction/**", "/inquiry/admin/**").hasRole("ADMIN")
                .requestMatchers("/member/general/**", "/safety/risk-factor/**", "/process/work-discussion/**", "/inquiry/**").hasRole("MANAGER")
                .requestMatchers("/main/**", "/settings/**", "/custom/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
//                .loginProcessingUrl("/auth/login")
//                .defaultSuccessUrl("/main")
                .successHandler((request, response, auth) -> {
                    String refererUrl = (String)request.getSession().getAttribute("previousUrl");
                    if(Strings.isNotBlank(refererUrl)) {
                        request.getSession().removeAttribute("previousUrl");

                        response.sendRedirect(refererUrl);
                    } else {
                        response.sendRedirect("/main");
                    }
                })
                .failureUrl("/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
            )
            .logout(logout -> logout
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/auth/login")
//                .addLogoutHandler((request, response, authentication) -> {
//                    // 사실 굳이 내가 세션 무효화하지 않아도 됨.
//                    // LogoutFilter가 내부적으로 해줌.
//                    HttpSession session = request.getSession();
//                    if (session != null) {
//                        session.invalidate();
//                    }
//                })  // 로그아웃 핸들러 추가
//                .logoutSuccessHandler((request, response, authentication) -> {
//                    response.sendRedirect("/auth/login");
//                }) // 로그아웃 성공 핸들러
                .logoutSuccessHandler((request, response, auth) -> {
                    String refererUrl = request.getHeader("Referer");  // 로그아웃 전 페이지 정보
                    if(Strings.isNotBlank(refererUrl)) {
                        request.getSession().setAttribute("previousUrl", refererUrl);
                    }
                    response.sendRedirect("/auth/login");  // 로그아웃 후 로그인 페이지로 이동
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
             .rememberMe(remember -> remember
                //  .key("uniqueAndSecretRememberMe")
                 .key("uniqueAndSecret")
                 .tokenValiditySeconds(60 * 60 * 24) // 24시간
//                 .rememberMeParameter("remember-me")          // 안해도 될듯~
//                 .rememberMeCookieName("remember-me-cookie")
                 .userDetailsService(userDetailsService)
                 .tokenRepository(persistentTokenRepository())
             )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService)
//            .passwordEncoder(passwordEncoder());
//    }

    @Bean
    PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(mysqlJpaMaster1DataSource);

        return repo;
    }
}
