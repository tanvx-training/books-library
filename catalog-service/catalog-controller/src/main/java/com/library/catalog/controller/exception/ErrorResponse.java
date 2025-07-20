package com.library.catalog.controller.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Standard error response DTO for API error responses.
 * Provides consistent error information across all endpoints.
 */
@Setter
@Getter
public class ErrorResponse {

    // Getters and Setters
    private String message;
    private String code;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    private String path;

    /**
     * Default constructor.
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with message.
     *
     * @param message the error message
     */
    public ErrorResponse(String message) {
        this();
        this.message = message;
    }

    /**
     * Constructor with message and code.
     *
     * @param message the error message
     * @param code the error code
     */
    public ErrorResponse(String message, String code) {
        this(message);
        this.code = code;
    }

    /**
     * Constructor with message, code, and path.
     *
     * @param message the error message
     * @param code the error code
     * @param path the request path where the error occurred
     */
    public ErrorResponse(String message, String code, String path) {
        this(message, code);
        this.path = path;
    }

}