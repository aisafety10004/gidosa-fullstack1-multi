package net.gidosa.full.webapp.configs.auth;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class ConstructionAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, ConstructionAuthenticationDetails> {
    
    @Override
    public ConstructionAuthenticationDetails buildDetails(HttpServletRequest request) {
        return new ConstructionAuthenticationDetails(request);
    }
} 