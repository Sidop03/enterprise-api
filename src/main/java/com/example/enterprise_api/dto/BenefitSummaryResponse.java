package com.example.enterprise_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BenefitSummaryResponse {
    @JsonProperty("user_id")
    private String userId;
    
    @JsonProperty("benefits")
    private List<BenefitResponse> benefits;
    
    @JsonProperty("total_active_benefits")
    private Integer totalActiveBenefits;
    
    @JsonProperty("generated_at")
    private String generatedAt;
    
    @JsonProperty("cache_hit")
    private Boolean cacheHit;
    
    @JsonProperty("request_id")
    private String requestId;
}
