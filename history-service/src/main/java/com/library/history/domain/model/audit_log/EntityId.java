package com.library.history.domain.model.audit_log;

import com.library.history.domain.exception.InvalidValueException;

public record EntityId(String value) {
    public EntityId {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidValueException("Entity ID cannot be empty");
        }
        if (value.length() > 255) {
            throw new InvalidValueException("Entity ID cannot exceed 255 characters");
        }
    }
}