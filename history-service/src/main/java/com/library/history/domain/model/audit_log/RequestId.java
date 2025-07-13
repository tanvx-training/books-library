package com.library.history.domain.model.audit_log;

import com.library.history.domain.exception.InvalidValueException;

public record RequestId(String value) {
    public RequestId {
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidValueException("Request ID cannot be empty");
        }
        if (value.length() > 100) {
            throw new InvalidValueException("Request ID cannot exceed 100 characters");
        }
    }
}