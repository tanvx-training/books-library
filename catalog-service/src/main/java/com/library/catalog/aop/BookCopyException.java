package com.library.catalog.aop;

import lombok.Getter;

@Getter
public class BookCopyException extends EntityServiceException {

    private static final String ENTITY_TYPE = "BookCopy";

    public BookCopyException(String message) {
        super(ENTITY_TYPE, null, message);
    }

    public BookCopyException(String operation, String message) {
        super(ENTITY_TYPE, operation, message);
    }

    public BookCopyException(String operation, String message, Throwable cause) {
        super(ENTITY_TYPE, operation, message, cause);
    }

    public static BookCopyException databaseError(String operation, Throwable cause) {
        return new BookCopyException(operation, 
            String.format("Failed to %s book copy due to database error", operation), cause);
    }

    public static BookCopyException mappingError(Throwable cause) {
        return new BookCopyException("mapping", "Failed to map book copy data", cause);
    }

    public static BookCopyException businessRuleViolation(String operation, String businessRule) {
        return new BookCopyException(operation, 
            String.format("Cannot %s book copy: %s", operation, businessRule));
    }

    public static BookCopyException concurrencyConflict(String operation, Long copyId) {
        return new BookCopyException(operation, 
            String.format("Concurrency conflict while trying to %s book copy with ID: %s", operation, copyId));
    }
}