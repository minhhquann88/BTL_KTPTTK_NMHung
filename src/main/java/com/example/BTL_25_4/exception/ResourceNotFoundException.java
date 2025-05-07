package com.example.BTL_25_4.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception được ném ra khi không tìm thấy một tài nguyên được yêu cầu.
 * Mặc định trả về HTTP Status 404 (Not Found).
 */
@ResponseStatus(HttpStatus.NOT_FOUND) // Trả về 404 Not Found
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}