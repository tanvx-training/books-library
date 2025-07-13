package com.library.history.domain.model.audit_log;

import com.library.history.domain.exception.InvalidValueException;

public enum ActionType {
    CREATE,
    UPDATE,
    DELETE;

    public static ActionType from(String value) {
        try {
            return ActionType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidValueException("Invalid action type: " + value);
        }
    }
} 