package com.library.book.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when author data violates domain rules.
 */
@Getter
public class InvalidAuthorDataException extends AuthorDomainException {

    private final String field;
    private final String reason;

    public InvalidAuthorDataException(String field, String reason) {
        super(String.format("Invalid author data for field '%s': %s", field, reason));
        this.field = field;
        this.reason = reason;
    }

}