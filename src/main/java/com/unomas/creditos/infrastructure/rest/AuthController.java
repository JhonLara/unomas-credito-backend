package com.unomas.creditos.infrastructure.rest;

import com.unomas.creditos.application.AuthService;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request.cedula(), request.email(), request.celular(), request.password()));
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request.cedula(), request.password()));
    }

    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, Object>> forgot(@RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request.cedula()));
    }

    @PostMapping("/password/reset")
    public ResponseEntity<Map<String, Object>> reset(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.resetPassword(request.cedula(), request.otp(), request.newPassword()));
    }

    public record RegisterRequest(@NotBlank String cedula, @Email String email, @NotBlank String celular, @Size(min = 8) String password) {}
    public record LoginRequest(@NotBlank String cedula, @NotBlank String password) {}
    public record ForgotPasswordRequest(@NotBlank String cedula) {}
    public record ResetPasswordRequest(@NotBlank String cedula, @NotBlank String otp, @Size(min = 8) String newPassword) {}
}
