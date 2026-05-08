package com.unomas.creditos.infrastructure.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Value("${app.payment.redirect-url}")
    private String redirectUrl;

    @GetMapping("/redirect-url")
    public ResponseEntity<Map<String, String>> redirectUrl() {
        return ResponseEntity.ok(Map.of("redirectUrl", redirectUrl));
    }
}
