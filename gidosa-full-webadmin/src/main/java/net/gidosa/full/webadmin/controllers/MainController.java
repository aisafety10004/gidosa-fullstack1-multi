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
import org.springframework.web.bind.annotation.RequestParam;

@Log4j2
@Controller
@RequiredArgsConstructor
//@RequestMapping("/main")
public class MainController {
    private final MainService mainService;
//    private final AuthService authService;

    @GetMapping({"/", "/main"})
    public String index(Model model, 
                       @AuthenticationPrincipal PrincipalDetails principalDetails,
                       @RequestParam(value = "error", required = false) String error) {
//        String test1 = authService.test1();

//        model.addAttribute("test1", test1);
        
        // 에러 파라미터가 있으면 모델에 추가
        if (error != null) {
            // 에러 코드에 따라 사용자 친화적인 메시지로 변환
            String errorMessage;
            switch (error) {
                case "no-construction":
                    errorMessage = "배정된 건설공사현장이 없습니다.";
                    break;
                case "menu-not-found":
                    errorMessage = "요청한 메뉴를 찾을 수 없습니다.";
                    break;
                case "invalid-menu-type":
                    errorMessage = "유효하지 않은 메뉴 타입입니다.";
                    break;
                default:
                    errorMessage = "오류가 발생했습니다: " + error;
                    break;
            }
            model.addAttribute("error", errorMessage);
        }
        
//        return PREFIX_THYMELEAF_BASE + "main/main";
//        return "thymeleaf/main/main";
        // return "main/intro/admin";
        // return "main/intro/manager";
        return "main/main";
    }

    @GetMapping("/error/403")
    public String accessDenied(){
        return "error/403";
    }
}
