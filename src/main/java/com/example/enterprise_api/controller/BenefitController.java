package com.example.enterprise_api.controller;

import com.example.enterprise_api.dto.BenefitSummaryResponse;
import com.example.enterprise_api.service.BenefitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@Tag(name = "Benefits API")
@SecurityRequirement(name = "bearerAuth")
public class BenefitController {

    @Autowired
    private BenefitService benefitService;

    @GetMapping("/{userId}/benefits")
    @PreAuthorize("hasAuthority('SCOPE_benefits.read')")
    @Operation(summary = "Get benefit summary for a user")
    public ResponseEntity<BenefitSummaryResponse> getBenefitSummary(
            @PathVariable @Parameter(example = "user123") String userId,
            @RequestParam(defaultValue = "summary") @Parameter(example = "summary") String profile,
            @RequestHeader(value = "X-Request-ID", required = false) String requestId) {

        log.info("Request - userId: {}, profile: {}", userId, profile);
        BenefitSummaryResponse response = benefitService.getBenefitSummary(userId, profile);
        return ResponseEntity.ok()
                .header("X-Request-ID", response.getRequestId())
                .header("Cache-Control", "private, max-age=300")
                .body(response);
    }
}