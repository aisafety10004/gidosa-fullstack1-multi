package net.gidosa.full.webapp.configs;

import lombok.RequiredArgsConstructor;
import net.gidosa.full.webapp.configs.auth.PrincipalDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalDetailsService principalDetailsService;

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
                .requestMatchers("/*", "/general/auth/**", "/general/member/**", "/general/document/**", "/css/**", "/js/**", "/assets/**", "/error/**", "/contact/**", "/construction/**", "/general/construction/*").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/general/auth/login")
                .defaultSuccessUrl("/general/main")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/")
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                    .accessDeniedPage("/error/403")
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
} 