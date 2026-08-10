package com.example.enterprise_api;

import com.example.enterprise_api.domain.Benefit;
import com.example.enterprise_api.domain.User;
import com.example.enterprise_api.repository.BenefitRepository;
import com.example.enterprise_api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureWebTestClient
class BenefitControllerIntegrationTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BenefitRepository benefitRepository;
    
    @BeforeEach
    void setUp() {
        // Clean up
        benefitRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create test user
        User user = new User();
        user.setId("test-user-123");
        user.setClientId("test-client-a");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        // Create test benefits
        Benefit benefit1 = new Benefit();
        benefit1.setId("b1");
        benefit1.setBenefitCode("HEALTH");
        benefit1.setName("Health Insurance");
        benefit1.setType("HEALTH");
        benefit1.setBalance(new BigDecimal("1200.50"));
        benefit1.setMaxBalance(new BigDecimal("2000.00"));
        benefit1.setEligibilityStatus("ACTIVE");
        benefit1.setUser(user);
        benefit1.setCreatedAt(LocalDateTime.now());
        benefit1.setUpdatedAt(LocalDateTime.now());
        benefitRepository.save(benefit1);
        
        Benefit benefit2 = new Benefit();
        benefit2.setId("b2");
        benefit2.setBenefitCode("RETIREMENT");
        benefit2.setName("401(k) Plan");
        benefit2.setType("RETIREMENT");
        benefit2.setBalance(new BigDecimal("45000.00"));
        benefit2.setMaxBalance(new BigDecimal("50000.00"));
        benefit2.setEligibilityStatus("ACTIVE");
        benefit2.setUser(user);
        benefit2.setCreatedAt(LocalDateTime.now());
        benefit2.setUpdatedAt(LocalDateTime.now());
        benefitRepository.save(benefit2);
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"SCOPE_benefits.read"})
    void getBenefitSummary_ReturnsSummary_WhenUserExists() {
        webTestClient
            .get()
            .uri("/v1/users/test-user-123/benefits?profile=summary")
            .header("Authorization", "Bearer test-token")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.user_id").isEqualTo("test-user-123")
            .jsonPath("$.total_active_benefits").isEqualTo(2)
            .jsonPath("$.benefits.length()").isEqualTo(2)
            .jsonPath("$.benefits[0].name").isEqualTo("Health Insurance")
            .jsonPath("$.cache_hit").isEqualTo(false)
            .jsonPath("$.request_id").isNotEmpty()
            .jsonPath("$.generated_at").isNotEmpty();
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"SCOPE_benefits.read"})
    void getBenefitSummary_ReturnsNotFound_WhenUserDoesNotExist() {
        webTestClient
            .get()
            .uri("/v1/users/nonexistent-user/benefits?profile=summary")
            .header("Authorization", "Bearer test-token")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.error_code").isEqualTo("USER_NOT_FOUND")
            .jsonPath("$.request_id").isNotEmpty();
    }
    
    @Test
    void getBenefitSummary_ReturnsUnauthorized_WhenNoAuthToken() {
        webTestClient
            .get()
            .uri("/v1/users/test-user-123/benefits?profile=summary")
            .exchange()
            .expectStatus().isUnauthorized();
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"SCOPE_benefits.read"})
    void getBenefitSummary_IncludesHeaders_InResponse() {
        webTestClient
            .get()
            .uri("/v1/users/test-user-123/benefits?profile=summary")
            .header("Authorization", "Bearer test-token")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().exists("X-Request-ID")
            .expectHeader().exists("ETag")
            .expectHeader().valueMatches("Cache-Control", ".*max-age=300.*");
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"SCOPE_benefits.read"})
    void getBenefitSummary_ReturnsCacheHitTrue_OnSecondRequest() {
        // First request - cache miss
        webTestClient
            .get()
            .uri("/v1/users/test-user-123/benefits?profile=summary")
            .header("Authorization", "Bearer test-token")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.cache_hit").isEqualTo(false);
        
        // Second request - cache hit
        webTestClient
            .get()
            .uri("/v1/users/test-user-123/benefits?profile=summary")
            .header("Authorization", "Bearer test-token")
            .exchange()
            .expectStatus().isOk()
            .expectBody()
            .jsonPath("$.cache_hit").isEqualTo(true);
    }
    
    @Test
    @WithMockUser(username = "testuser", authorities = {"SCOPE_benefits.read"})
    void health_ReturnsOk() {
        webTestClient
            .get()
            .uri("/v1/users/health")
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .isEqualTo("OK");
    }
}
