package com.marmitech.Marmitech.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        String podName = System.getenv().getOrDefault("HOSTNAME", "local-pod");
        return ResponseEntity.ok("OK - Pod: " + podName);
    }
}