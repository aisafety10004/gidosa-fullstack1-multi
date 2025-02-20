package net.gidosa.full.webadmin.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.configs.auth.PrincipalDetails;
import net.gidosa.full.webadmin.services.AuthService;
import net.gidosa.full.webadmin.services.MainService;
import net.gidosa.rdb.models.entities.dbs.mysql.Construction;
import net.gidosa.rdb.models.entities.dbs.mysql.MemberAdmin;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Objects;

@Log4j2
@Controller
@RequiredArgsConstructor
//@RequestMapping("/main")
public class MainController {
    private final MainService mainService;
//    private final AuthService authService;

    @GetMapping({"/", "/main"})
    public String index(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        String test1 = authService.test1();

//        model.addAttribute("test1", test1);
//        return PREFIX_THYMELEAF_BASE + "main/main";
//        return "thymeleaf/main/main";
        return "main/main";
    }

    @GetMapping("/error/403")
    public String accessDenied(){
        return "error/403";
    }
}
