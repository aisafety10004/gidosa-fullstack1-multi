package net.gidosa.full.webadmin.configs;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class LayoutAdvice {

    @ModelAttribute("layout")
    public String getLayout(@AuthenticationPrincipal UserDetails userDetails, Model model, HttpServletRequest request) {
        if (userDetails != null) {
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"))) {
                model.addAttribute("url", request.getRequestURI());
                return "layouts/header/managerHeader";
            }
            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                return "layouts/header/adminHeader";
            }
        }
        // return "layouts/admin";
        return "layouts/etc1";
    }
}