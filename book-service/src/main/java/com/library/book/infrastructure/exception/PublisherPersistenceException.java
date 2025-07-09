package com.library.book.infrastructure.exception;

/**
 * Exception thrown when there's an issue with publisher persistence.
 */
public class PublisherPersistenceException extends RuntimeException {

    public PublisherPersistenceException(String message) {
        super(message);
    }

    public PublisherPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}