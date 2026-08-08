package com.example.enterprise_api.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "benefits", schema = "public",
        indexes = {
                @Index(name = "idx_benefit_user_id", columnList = "user_id"),
                @Index(name = "idx_benefit_eligibility", columnList = "eligibilityStatus")
        })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Benefit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String benefitCode;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private BigDecimal balance;

    private BigDecimal maxBalance;

    @Column(nullable = false)
    private String eligibilityStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}