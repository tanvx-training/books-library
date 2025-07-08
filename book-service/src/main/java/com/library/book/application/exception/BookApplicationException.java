package com.library.book.application.exception;

/**
 * Base exception for all book-related application exceptions.
 */
public class BookApplicationException extends RuntimeException {

    public BookApplicationException(String message) {
        super(message);
    }

    public BookApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
} 