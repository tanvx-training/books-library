package com.library.history.domain.model.audit_log;

import com.library.history.domain.exception.InvalidValueException;

public record UserId(String value) {

    public UserId {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidValueException("User ID cannot be empty");
        }
        if (value.length() > 36) {
            throw new InvalidValueException("User ID cannot exceed 36 characters");
        }
    }
} 