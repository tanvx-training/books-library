package com.library.history.domain.exception;

public class AuditLogNotFoundException extends DomainException {
    
    public AuditLogNotFoundException(String message) {
        super(message);
    }
    
    public AuditLogNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
} 