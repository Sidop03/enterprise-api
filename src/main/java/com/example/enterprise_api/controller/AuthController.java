package com.example.enterprise_api.controller;

import com.example.enterprise_api.dto.LoginRequest;
import com.example.enterprise_api.dto.SignupRequest;
import com.example.enterprise_api.dto.TokenResponse;
import com.example.enterprise_api.service.JwtService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @PostConstruct
    public void init() {
        System.out.println("🚀🚀🚀 AuthV2Controller is LOADED! 🚀🚀🚀");
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("status", "AuthV2 is ALIVE! 🚀");
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
        System.out.println("📝 Login request for: " + request.getUsername());
        return ResponseEntity.ok(jwtService.generateToken(request));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        System.out.println("📝 Signup request for email: " + request.getEmail());
        String message = jwtService.registerUser(request);
        return ResponseEntity.ok(message);
    }
}