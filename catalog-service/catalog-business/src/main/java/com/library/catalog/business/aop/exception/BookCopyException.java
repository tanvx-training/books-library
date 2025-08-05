package com.library.catalog.business.aop.exception;

import lombok.Getter;

/**
 * Exception class for BookCopy-related business logic errors.
 * Extends EntityServiceException to provide specific error handling for book copy operations.
 */
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

    /**
     * Creates an exception for database-related errors during book copy operations.
     *
     * @param operation the operation being performed
     * @param cause     the underlying cause
     * @return BookCopyException with database error details
     */
    public static BookCopyException databaseError(String operation, Throwable cause) {
        return new BookCopyException(operation, 
            String.format("Failed to %s book copy due to database error", operation), cause);
    }

    /**
     * Creates an exception for mapping errors during book copy operations.
     *
     * @param cause the underlying cause
     * @return BookCopyException with mapping error details
     */
    public static BookCopyException mappingError(Throwable cause) {
        return new BookCopyException("mapping", "Failed to map book copy data", cause);
    }

    /**
     * Creates an exception for business logic violations.
     *
     * @param operation    the operation being performed
     * @param businessRule the business rule that was violated
     * @return BookCopyException with business rule violation details
     */
    public static BookCopyException businessRuleViolation(String operation, String businessRule) {
        return new BookCopyException(operation, 
            String.format("Cannot %s book copy: %s", operation, businessRule));
    }

    /**
     * Creates an exception for concurrency conflicts during book copy operations.
     *
     * @param operation the operation being performed
     * @param copyId    the book copy ID
     * @return BookCopyException with concurrency conflict details
     */
    public static BookCopyException concurrencyConflict(String operation, Long copyId) {
        return new BookCopyException(operation, 
            String.format("Concurrency conflict while trying to %s book copy with ID: %s", operation, copyId));
    }
}