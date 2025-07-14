package com.library.user.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when user data is invalid
 */
@Getter
public class InvalidUserDataException extends DomainException {

    private final String field;
    private final String reason;

    public InvalidUserDataException(String field, String reason) {
        super(String.format("Invalid user data for field '%s': %s", field, reason));
        this.field = field;
        this.reason = reason;
    }
    
    public InvalidUserDataException(String message) {
        super(message);
        this.field = null;
        this.reason = message;
    }
}