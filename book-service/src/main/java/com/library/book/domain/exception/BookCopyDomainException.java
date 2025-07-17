package com.library.book.domain.exception;

/**
 * Exception thrown when a business rule related to Book is violated.
 */
public class BookCopyDomainException extends DomainException {

    public BookCopyDomainException(String message) {
        super(message);
    }

    public BookCopyDomainException(String message, Throwable cause) {
        super(message, cause);
    }
} 