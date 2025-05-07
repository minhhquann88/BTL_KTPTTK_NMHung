package com.example.BTL_25_4.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception được ném ra khi xe được yêu cầu đặt không sẵn có
 * (do trạng thái is_available=false hoặc đã có booking trùng lặp).
 * Mặc định trả về HTTP Status 409 (Conflict) vì tài nguyên xe tồn tại
 * nhưng yêu cầu hiện tại mâu thuẫn với trạng thái của nó.
 */
@ResponseStatus(HttpStatus.CONFLICT) // Trả về 409 Conflict
public class CarNotAvailableException extends RuntimeException {

    public CarNotAvailableException(String message) {
        super(message);
    }

    public CarNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }
}