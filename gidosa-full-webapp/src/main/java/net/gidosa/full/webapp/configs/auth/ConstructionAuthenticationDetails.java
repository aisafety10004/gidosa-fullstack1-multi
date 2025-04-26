package net.gidosa.full.webapp.configs.auth;

import lombok.Getter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import jakarta.servlet.http.HttpServletRequest;

@Getter
public class ConstructionAuthenticationDetails extends WebAuthenticationDetails {
    
    private final Long constructionId;
    
    public ConstructionAuthenticationDetails(HttpServletRequest request) {
        super(request);
        String constructionIdStr = request.getParameter("constructionId");
        this.constructionId = constructionIdStr != null ? Long.parseLong(constructionIdStr) : null;
    }
} 