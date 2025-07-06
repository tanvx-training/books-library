package com.library.book.domain.exception;

/**
 * Exception thrown when a business rule related to Author is violated.
 */
public class AuthorDomainException extends DomainException {

    public AuthorDomainException(String message) {
        super(message);
    }

    public AuthorDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}