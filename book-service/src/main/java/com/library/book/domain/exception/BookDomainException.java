package com.library.book.domain.exception;

/**
 * Exception thrown when a business rule related to Book is violated.
 */
public class BookDomainException extends DomainException {

    public BookDomainException(String message) {
        super(message);
    }

    public BookDomainException(String message, Throwable cause) {
        super(message, cause);
    }
} 