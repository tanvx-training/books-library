package com.library.member.aop;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String message;
    private String code;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;
    
    private String correlationId;

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String message) {
        this();
        this.message = message;
    }

    public ErrorResponse(String message, String code) {
        this(message);
        this.code = code;
    }

    public ErrorResponse(String message, String code, String path) {
        this(message, code);
        this.path = path;
    }

    public ErrorResponse(String message, String code, String path, String correlationId) {
        this(message, code, path);
        this.correlationId = correlationId;
    }

}