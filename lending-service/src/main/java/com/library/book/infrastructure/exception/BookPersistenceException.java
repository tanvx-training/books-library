package com.library.book.infrastructure.exception;

/**
 * Exception thrown when there's an issue with book persistence.
 */
public class BookPersistenceException extends RuntimeException {

    public BookPersistenceException(String message) {
        super(message);
    }

    public BookPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
} 