package com.library.book.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when category data violates domain rules.
 */
@Getter
public class InvalidCategoryDataException extends CategoryDomainException {

    private final String field;
    private final String reason;

    public InvalidCategoryDataException(String field, String reason) {
        super(String.format("Invalid category data for field '%s': %s", field, reason));
        this.field = field;
        this.reason = reason;
    }
    
    public InvalidCategoryDataException(String message) {
        super(message);
        this.field = null;
        this.reason = message;
    }
}