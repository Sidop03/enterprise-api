package com.example.enterprise_api.exception;

import com.example.enterprise_api.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
        String requestId = UUID.randomUUID().toString();
        log.warn("User not found - userId: {}, requestId: {}", ex.getUserId(), requestId);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header("X-Request-ID", requestId)
                .body(ErrorResponse.builder()
                        .errorCode(ex.getErrorCode()).message(ex.getMessage()).requestId(requestId)
                        .timestamp(LocalDateTime.now().format(ISO_FORMATTER))
                        .details("User does not exist or belongs to different client").build());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        String requestId = UUID.randomUUID().toString();
        log.warn("Access denied - requestId: {}", requestId);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .header("X-Request-ID", requestId)
                .body(ErrorResponse.builder()
                        .errorCode(ex.getErrorCode()).message(ex.getMessage()).requestId(requestId)
                        .timestamp(LocalDateTime.now().format(ISO_FORMATTER))
                        .details("Insufficient permissions").build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        String requestId = UUID.randomUUID().toString();
        log.error("Unexpected error - requestId: {}", requestId, ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("X-Request-ID", requestId)
                .body(ErrorResponse.builder()
                        .errorCode("INTERNAL_SERVER_ERROR")
                        .message("An unexpected error occurred")
                        .requestId(requestId)
                        .timestamp(LocalDateTime.now().format(ISO_FORMATTER))
                        .details("Contact support with Request ID: " + requestId)
                        .build());
    }
}