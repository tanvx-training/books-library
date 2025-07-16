package com.library.book.domain.exception;

import lombok.Getter;

/**
 * Exception thrown when publisher data violates domain rules.
 */
@Getter
public class InvalidPublisherDataException extends PublisherDomainException {

    private final String field;
    private final String reason;

    public InvalidPublisherDataException(String field, String reason) {
        super(String.format("Invalid publisher data for field '%s': %s", field, reason));
        this.field = field;
        this.reason = reason;
    }
    
    public InvalidPublisherDataException(String message) {
        super(message);
        this.field = null;
        this.reason = message;
    }

}