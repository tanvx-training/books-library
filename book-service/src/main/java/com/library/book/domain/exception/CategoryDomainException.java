package com.library.book.domain.exception;

/**
 * Exception thrown when a business rule related to Category is violated.
 */
public class CategoryDomainException extends DomainException {

    public CategoryDomainException(String message) {
        super(message);
    }

    public CategoryDomainException(String message, Throwable cause) {
        super(message, cause);
    }
}