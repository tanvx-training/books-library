package com.library.book.application.exception;

/**
 * Base exception for all category-related application exceptions.
 */
public class CategoryApplicationException extends RuntimeException {

    public CategoryApplicationException(String message) {
        super(message);
    }

    public CategoryApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}