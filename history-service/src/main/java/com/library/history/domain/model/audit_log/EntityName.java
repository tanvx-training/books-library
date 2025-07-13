package com.library.history.domain.model.audit_log;

import com.library.history.domain.exception.InvalidValueException;

public record EntityName(String value) {
    public EntityName {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidValueException("Entity name cannot be empty");
        }
        if (value.length() > 100) {
            throw new InvalidValueException("Entity name cannot exceed 100 characters");
        }
    }
}