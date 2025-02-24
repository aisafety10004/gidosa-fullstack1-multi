package net.gidosa.full.webapp.configs;

import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.configs.auth.PrincipalDetails;
import net.gidosa.full.webapp.configs.auth.PrincipalDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;

import java.util.Objects;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalDetailsService principalDetailsService;
    
//    @Value("${spring.servlet.session.cookie.http-only:true}")
//    private boolean cookieHttpOnly;
    
    @Value("${spring.servlet.session.cookie.secure:false}") 
    private boolean cookieSecure;
    
//    @Value("${spring.servlet.session.timeout:30m}")
//    private String sessionTimeout;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
//            .csrf(csrf -> csrf.disable())  // CSRF 보호는 필요에 따라 활성화하세요
            .authorizeHttpRequests(auth -> auth
                //.requestMatchers("/*", "/general/auth/**", "/general/member/**", "/general/document/**", "/css/**", "/js/**", "/assets/**", "/error/**", "/contact/**", "/construction/**", "/general/construction/*").permitAll()
                .requestMatchers("/*", "/general/document/**", "/css/**", "/js/**", "/assets/**", "/error/**", "/contact/**", "/construction/**", "/general/construction/*").permitAll()
                .requestMatchers("/general/auth/login", "/general/member/register/**", "/general/member/find-*").hasRole("ANONYMOUS")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/general/auth/login")
                .failureHandler((request, response, exception) -> {
                    String constructionId = request.getParameter("constructionId");
                    response.sendRedirect("/general/auth/login?constructionId=" + constructionId + "&error");
                })
//                .successHandler((request, response, authentication) -> {
//                    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//                    Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
//                    response.sendRedirect("/general/main?constructionId=" + constructionId);
//                })
                .defaultSuccessUrl("/general/main")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/general/auth/logout")   // post로 날려야 함
                .logoutSuccessHandler((request, response, authentication) -> {
                    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
                    Long constructionId = principalDetails.getMemberGeneral().getConstruction().getId();
                    response.sendRedirect("/general/auth/login?constructionId=" + constructionId);
                })
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    // Authentication 객체 가져오기
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//                    if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
//                        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//                    } else {
//                        response.sendRedirect("/error/403");
//                    }
                    // 이제 principalDetails 사용 가능
                    if (!Objects.isNull(authentication) && authentication.isAuthenticated() &&
                          (request.getRequestURI().startsWith("/general/auth/login") ||
                           request.getRequestURI().startsWith("/general/member/find-") ||
                           request.getRequestURI().startsWith("/general/member/register"))
                    ) {
                        response.sendRedirect("/general/main");
                    } else {
                        response.sendRedirect("/error/403");
                    }
                })
            )
            .rememberMe(remember -> remember
                .key("uniqueAndSecretRememberMeKey")
                .tokenValiditySeconds(60 * 60 * 24 * 30)
                .rememberMeParameter("remember-me")
                .userDetailsService(principalDetailsService)
                .rememberMeCookieName("remember-me-cookie")
                .useSecureCookie(cookieSecure)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 