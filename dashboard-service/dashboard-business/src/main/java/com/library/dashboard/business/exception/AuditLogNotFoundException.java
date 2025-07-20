package com.library.dashboard.business.exception;

import java.util.UUID;

/**
 * Exception thrown when an audit log entry is not found.
 * This exception is typically thrown when attempting to retrieve an audit log by ID
 * that does not exist in the database.
 */
public class AuditLogNotFoundException extends AuditLogException {
    
    /**
     * Constructs a new AuditLogNotFoundException for the specified audit log ID.
     *
     * @param id the UUID of the audit log that was not found
     */
    public AuditLogNotFoundException(UUID id) {
        super("Audit log not found with id: " + id);
    }
    
    /**
     * Constructs a new AuditLogNotFoundException with a custom message.
     *
     * @param message the detail message
     */
    public AuditLogNotFoundException(String message) {
        super(message);
    }
}