package com.library.history.infrastructure.exception;

public class AuditLogPersistenceException extends RuntimeException {
    
    public AuditLogPersistenceException(String message) {
        super(message);
    }
    
    public AuditLogPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}