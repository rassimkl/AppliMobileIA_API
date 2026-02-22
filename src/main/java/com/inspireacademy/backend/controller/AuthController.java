package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.dto.AuthResponse;
import com.inspireacademy.backend.dto.LoginRequest;
import com.inspireacademy.backend.dto.RegisterRequest;
import com.inspireacademy.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // Injection du service d'authentification
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 🔐 Endpoint LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        AuthResponse response = authService.login(
                request.getEmail(),
                request.getPassword()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @RequestBody @Valid RegisterRequest request
    ) {
        return ResponseEntity.ok(authService.register(request));
    }
}
