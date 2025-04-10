package net.gidosa.full.webadmin.configs;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collection;

@ControllerAdvice
public class LayoutAdvice {
    
    @ModelAttribute("layout")
    public String getLayout(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
                return "layouts/manager";
            }
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "layouts/admin";
            }
        }
//        return "layouts/admin";
        return "layouts/etc1";
    }
} 