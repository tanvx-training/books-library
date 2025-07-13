package com.library.history.domain.exception;

public class InvalidValueException extends DomainException {
    public InvalidValueException(String message) {
        super(message);
    }

    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }
} 