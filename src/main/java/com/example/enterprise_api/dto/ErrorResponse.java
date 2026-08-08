package com.example.enterprise_api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    @JsonProperty("error_code")
    private String errorCode;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("request_id")
    private String requestId;
    
    @JsonProperty("timestamp")
    private String timestamp;
    
    @JsonProperty("details")
    private String details;
}
