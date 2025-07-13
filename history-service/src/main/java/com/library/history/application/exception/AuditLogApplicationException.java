package com.library.history.application.exception;

public class AuditLogApplicationException extends RuntimeException {
    
    public AuditLogApplicationException(String message) {
        super(message);
    }
    
    public AuditLogApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 