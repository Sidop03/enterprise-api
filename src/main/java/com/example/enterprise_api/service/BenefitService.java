package com.example.enterprise_api.service;

import com.example.enterprise_api.domain.Benefit;
import com.example.enterprise_api.dto.BenefitResponse;
import com.example.enterprise_api.dto.BenefitSummaryResponse;
import com.example.enterprise_api.exception.UserNotFoundException;
import com.example.enterprise_api.repository.BenefitRepository;
import com.example.enterprise_api.util.CacheUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Slf4j
@Service
public class BenefitService {

    @Autowired private BenefitRepository benefitRepository;
    @Autowired private CacheUtil cacheUtil;
    @Autowired private JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public BenefitSummaryResponse getBenefitSummary(String userId, String profile) {
        String requestId = UUID.randomUUID().toString();
        String clientId = getClientIdFromToken();

        log.info("Fetching benefits - userId: {}, clientId: {}", userId, clientId);

        // Cache check
        String cacheKey = "benefits:" + userId + ":" + profile;
        BenefitSummaryResponse cached = cacheUtil.get(cacheKey, BenefitSummaryResponse.class);
        if (cached != null) {
            cached.setCacheHit(true);
            cached.setRequestId(requestId);
            return cached;
        }

        // Client validation via SQL
        try {
            String actualClientId = jdbcTemplate.queryForObject(
                    "SELECT TRIM(client_id) FROM public.users WHERE id = ?", String.class, userId);
            log.info("🔍 JDBC fetched clientId: '{}' for userId: '{}'", actualClientId, userId);
            log.info("🔍 Token clientId: '{}'", clientId);

            if (actualClientId == null || !actualClientId.trim().equals(clientId.trim())) {
                log.error("❌ Client mismatch: DB='{}', Token='{}'", actualClientId, clientId);
                throw new UserNotFoundException(userId);
            }
        } catch (EmptyResultDataAccessException ex) {
            log.error("❌ No user found in JDBC for userId: {}", userId);
            throw new UserNotFoundException(userId);
        }

        List<Benefit> benefits = benefitRepository.findByUserId(userId);
        List<BenefitResponse> benefitResponses = benefits.stream()
                .map(this::toBenefitResponse).collect(Collectors.toList());

        int activeCount = (int) benefits.stream().filter(b -> "ACTIVE".equals(b.getEligibilityStatus())).count();

        BenefitSummaryResponse response = BenefitSummaryResponse.builder()
                .userId(userId).benefits(benefitResponses).totalActiveBenefits(activeCount)
                .generatedAt(LocalDateTime.now().format(ISO_FORMATTER)).cacheHit(false).requestId(requestId).build();

        cacheUtil.set(cacheKey, response, 300);
        return response;
    }

    private BenefitResponse toBenefitResponse(Benefit benefit) {
        return BenefitResponse.builder().id(benefit.getId()).benefitCode(benefit.getBenefitCode())
                .name(benefit.getName()).type(benefit.getType()).balance(benefit.getBalance())
                .maxBalance(benefit.getMaxBalance()).eligibilityStatus(benefit.getEligibilityStatus()).build();
    }

    private String getClientIdFromToken() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            log.warn("⚠️ No authentication found in SecurityContext");
            return "UNKNOWN";
        }

        // 🔥 Custom JwtAuthenticationFilter se aaya hua token
        if (auth instanceof UsernamePasswordAuthenticationToken) {
            Object details = auth.getDetails();
            if (details instanceof String && !((String) details).isBlank()) {
                log.info("✅ Extracted clientId from custom filter: {}", details);
                return (String) details;
            }
        }

        // Fallback (agar koi aur type ho)
        log.warn("⚠️ Unknown Authentication type: {}", auth.getClass().getSimpleName());
        return "UNKNOWN";
    }
}