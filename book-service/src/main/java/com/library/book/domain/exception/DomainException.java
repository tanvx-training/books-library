package com.library.book.domain.exception;

/**
 * Base exception class for all domain exceptions.
 * Domain exceptions represent business rule violations.
 */
public abstract class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}