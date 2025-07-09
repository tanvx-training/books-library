package com.library.book.application.exception;

/**
 * Base exception for all publisher-related application exceptions.
 */
public class PublisherApplicationException extends RuntimeException {

    public PublisherApplicationException(String message) {
        super(message);
    }

    public PublisherApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}