package com.library.book.application.exception;

/**
 * Base exception for all author-related application exceptions.
 */
public class AuthorApplicationException extends RuntimeException {

    public AuthorApplicationException(String message) {
        super(message);
    }

    public AuthorApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}