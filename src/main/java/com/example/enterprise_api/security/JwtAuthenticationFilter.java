package com.example.enterprise_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final SecretKey key;
    private final String[] publicPaths;

    public JwtAuthenticationFilter(String secret, String[] publicPaths) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.publicPaths = publicPaths;
        log.info("🔐 Filter initialized with public paths");
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String servletPath = request.getServletPath();

        if (publicPaths == null) return false;

        for (String path : publicPaths) {
            if (path.endsWith("/**")) {
                String prefix = path.substring(0, path.length() - 3);
                if (servletPath.startsWith(prefix)) {
                    log.info("🔓 Skipping filter for public path: {}", servletPath);
                    return true;
                }
            } else if (servletPath.equals(path)) {
                log.info("🔓 Skipping filter for exact public path: {}", servletPath);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "error_code": "MISSING_TOKEN",
                    "message": "Authorization header is required",
                    "timestamp": "%s"
                }
                """.formatted(java.time.Instant.now()));
            return;
        }

        String token = authHeader.substring(7);
        log.info("🔍 Verifying token: {}...", token.substring(0, Math.min(token.length(), 20)));

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String userId = claims.getSubject();
            String clientId = claims.get("client_id", String.class);
            String scope = claims.get("scope", String.class);

            log.info("✅ Token verified: userId={}", userId);

            List<SimpleGrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("SCOPE_" + scope)
            );

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            authToken.setDetails(clientId);
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            log.error("❌ Token verification failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("""
                {
                    "error_code": "INVALID_TOKEN",
                    "message": "Invalid or expired JWT token",
                    "timestamp": "%s"
                }
                """.formatted(java.time.Instant.now()));
            return;
        }

        filterChain.doFilter(request, response);
    }
}