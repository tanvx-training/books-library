package com.library.book.infrastructure.exception;

/**
 * Exception thrown when there's an issue with category persistence.
 */
public class CategoryPersistenceException extends RuntimeException {

    public CategoryPersistenceException(String message) {
        super(message);
    }

    public CategoryPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}