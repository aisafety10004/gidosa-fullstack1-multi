package net.gidosa.full.webadmin.configs;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.gidosa.full.webadmin.services.CustomUserDetailsService;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/assets/**", "/vendor/**", "/error/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/member/admin/**", "/construction/**", "/request/construction/**").hasRole("ADMIN")
                .requestMatchers("/member/general/**").hasAnyRole("MANAGER")
                .requestMatchers("/main/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/main")
                .failureUrl("/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
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
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // .rememberMe(remember -> remember
            //     .key("uniqueAndSecret")
            //     .tokenValiditySeconds(86400) // 24시간
            //     .rememberMeParameter("remember-me")
            //     .userDetailsService(userDetailsService)
            // )
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
}
