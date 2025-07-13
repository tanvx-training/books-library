package com.library.history.domain.model.audit_log;

import com.library.history.domain.exception.InvalidValueException;

public record ServiceName(String value) {
    public ServiceName {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidValueException("Service name cannot be empty");
        }
        if (value.length() > 100) {
            throw new InvalidValueException("Service name cannot exceed 100 characters");
        }
    }
} 