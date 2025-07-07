package com.library.book.domain.exception;

/**
 * Exception thrown when a business rule related to Publisher is violated.
 */
public class PublisherDomainException extends DomainException {

    public PublisherDomainException(String message) {
        super(message);
    }

    public PublisherDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}