package com.example.enterprise_api.service;

import com.example.enterprise_api.domain.User;
import com.example.enterprise_api.dto.LoginRequest;
import com.example.enterprise_api.dto.SignupRequest;
import com.example.enterprise_api.dto.TokenResponse;
import com.example.enterprise_api.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private final SecretKey key;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 🔥 All values from properties (Zero hardcoding)
    @Value("${app.default.client-id}")
    private String defaultClientId;

    @Value("${app.jwt.issuer}")
    private String issuer;

    @Value("${app.jwt.default-scope}")
    private String defaultScope;

    @Value("${app.jwt.expiration-seconds}")
    private long jwtExpiration;

    public JwtService(@Value("${spring.security.oauth2.resourceserver.jwt.secret}") String secret,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public TokenResponse generateToken(LoginRequest request) {
        log.info("🔍 Attempting login for username: {}", request.getUsername());

        User user = userRepository.findByEmailIgnoreCase(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("User account is not active");
        }

        Instant now = Instant.now();
        String accessToken = Jwts.builder()
                .subject(user.getId())
                .issuer(issuer)  // ✅ Dynamic
                .claim("client_id", user.getClientId())
                .claim("scope", defaultScope)  // ✅ Dynamic
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(jwtExpiration, ChronoUnit.SECONDS))) // ✅ Dynamic
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken("dummy-refresh-token")
                .expiresIn((int) jwtExpiration)
                .tokenType("Bearer")
                .build();
    }

    public String registerUser(SignupRequest request) {
        if (userRepository.findByEmailIgnoreCase(request.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists!");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setClientId(defaultClientId);  // ✅ Dynamic
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("✅ New user registered: {}", user.getEmail());
        return "User registered successfully! Please login.";
    }
}