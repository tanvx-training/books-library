package com.library.user.application.exception;

public class UserApplicationException extends RuntimeException {
    
    public UserApplicationException(String message) {
        super(message);
    }
    
    public UserApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 