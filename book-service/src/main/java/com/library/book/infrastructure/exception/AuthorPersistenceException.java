package com.library.book.infrastructure.exception;

/**
 * Exception thrown when there's an issue with author persistence.
 */
public class AuthorPersistenceException extends RuntimeException {

    public AuthorPersistenceException(String message) {
        super(message);
    }

    public AuthorPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}