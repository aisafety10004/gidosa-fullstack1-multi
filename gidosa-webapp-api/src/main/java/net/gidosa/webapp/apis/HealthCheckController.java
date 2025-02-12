package net.gidosa.webapp.apis;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    //    @GetMapping("/")
    @GetMapping
    public ResponseEntity index() {
//        try {
//            throw new Exception("This is a api-webapp test.");
//        } catch (Exception e) {
//            Sentry.captureException(e);
//        }
        return ResponseEntity.ok("success~");
    }
}
