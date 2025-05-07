package com.example.BTL_25_4.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception chung cho các lỗi validation nghiệp vụ hoặc dữ liệu không hợp lệ
 * không thuộc các loại exception cụ thể khác.
 * Mặc định trả về HTTP Status 400 (Bad Request).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST) // Trả về 400 Bad Request
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}