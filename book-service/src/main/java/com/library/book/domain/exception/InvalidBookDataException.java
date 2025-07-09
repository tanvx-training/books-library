package com.library.book.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when book data violates domain rules.
 */
@Getter
public class InvalidBookDataException extends BookDomainException {

    private final String field;
    private final String reason;

    public InvalidBookDataException(String field, String reason) {
        super(String.format("Invalid book data for field '%s': %s", field, reason));
        this.field = field;
        this.reason = reason;
    }
} 