package com.example.enterprise_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitResponse {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("benefit_code")
    private String benefitCode;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("balance")
    private BigDecimal balance;
    
    @JsonProperty("max_balance")
    private BigDecimal maxBalance;
    
    @JsonProperty("eligibility_status")
    private String eligibilityStatus;
}
