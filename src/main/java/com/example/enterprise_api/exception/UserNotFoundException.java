package com.example.enterprise_api.exception;

public class UserNotFoundException extends RuntimeException {
    private String userId;
    private String errorCode = "USER_NOT_FOUND";
    
    public UserNotFoundException(String userId) {
        super("User not found: " + userId);
        this.userId = userId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
