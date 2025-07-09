package com.library.book.infrastructure.exception;

/**
 * Exception thrown when there's an issue with book persistence.
 */
public class BookCopyPersistenceException extends RuntimeException {

    public BookCopyPersistenceException(String message) {
        super(message);
    }

    public BookCopyPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
} 