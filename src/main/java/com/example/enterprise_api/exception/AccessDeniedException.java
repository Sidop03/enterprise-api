package com.example.enterprise_api.exception;

public class AccessDeniedException extends RuntimeException {
    private String errorCode = "ACCESS_DENIED";
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
