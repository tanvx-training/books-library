package com.library.dashboard.business.exception;

/**
 * Base exception class for audit log related errors.
 * This serves as the parent class for all audit log specific exceptions.
 */
public class AuditLogException extends RuntimeException {
    
    /**
     * Constructs a new AuditLogException with the specified detail message.
     *
     * @param message the detail message
     */
    public AuditLogException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new AuditLogException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public AuditLogException(String message, Throwable cause) {
        super(message, cause);
    }
}