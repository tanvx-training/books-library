package com.library.dashboard.aop;

import java.util.UUID;

public class AuditLogNotFoundException extends AuditLogException {

    public AuditLogNotFoundException(UUID id) {
        super("Audit log not found with id: " + id);
    }

    public AuditLogNotFoundException(String message) {
        super(message);
    }
}