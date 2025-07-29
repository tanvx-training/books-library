package com.library.catalog.business.aop.exception;

import lombok.Getter;

/**
 * Exception class for duplicate copy number violations.
 * Extends EntityValidationException to handle unique constraint violations for book copy numbers.
 */
@Getter
public class DuplicateCopyNumberException extends EntityValidationException {

    private final Long bookId;
    private final String copyNumber;

    public DuplicateCopyNumberException(Long bookId, String copyNumber) {
        super(String.format("Copy number '%s' already exists for book ID: %s", copyNumber, bookId));
        this.bookId = bookId;
        this.copyNumber = copyNumber;
    }

    public DuplicateCopyNumberException(Long bookId, String copyNumber, String message) {
        super(message);
        this.bookId = bookId;
        this.copyNumber = copyNumber;
    }

    public DuplicateCopyNumberException(Long bookId, String copyNumber, String message, Throwable cause) {
        super(message, cause);
        this.bookId = bookId;
        this.copyNumber = copyNumber;
    }

    /**
     * Creates an exception for duplicate copy number during creation.
     *
     * @param bookId     the book ID
     * @param copyNumber the duplicate copy number
     * @return DuplicateCopyNumberException with creation context
     */
    public static DuplicateCopyNumberException forCreation(Long bookId, String copyNumber) {
        String message = String.format("Cannot create book copy: copy number '%s' already exists for book ID: %s", 
                                      copyNumber, bookId);
        return new DuplicateCopyNumberException(bookId, copyNumber, message);
    }

    /**
     * Creates an exception for duplicate copy number during update.
     *
     * @param bookId     the book ID
     * @param copyNumber the duplicate copy number
     * @param copyId     the copy ID being updated
     * @return DuplicateCopyNumberException with update context
     */
    public static DuplicateCopyNumberException forUpdate(Long bookId, String copyNumber, Long copyId) {
        String message = String.format("Cannot update book copy ID %s: copy number '%s' already exists for book ID: %s", 
                                      copyId, copyNumber, bookId);
        return new DuplicateCopyNumberException(bookId, copyNumber, message);
    }

    /**
     * Creates an exception for database constraint violations.
     *
     * @param bookId     the book ID
     * @param copyNumber the duplicate copy number
     * @param cause      the underlying database exception
     * @return DuplicateCopyNumberException with database context
     */
    public static DuplicateCopyNumberException fromDatabaseConstraint(Long bookId, String copyNumber, Throwable cause) {
        String message = String.format("Database constraint violation: copy number '%s' already exists for book ID: %s", 
                                      copyNumber, bookId);
        return new DuplicateCopyNumberException(bookId, copyNumber, message, cause);
    }
}