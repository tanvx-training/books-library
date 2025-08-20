package com.library.notification.aop;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ErrorResponse {

    private String message;
    private String errorCode;
    private String path;
    private String correlationId;
    private LocalDateTime timestamp;

    public ErrorResponse(String message, String errorCode, String path, String correlationId) {
        this.message = message;
        this.errorCode = errorCode;
        this.path = path;
        this.correlationId = correlationId;
        this.timestamp = LocalDateTime.now();
    }
}