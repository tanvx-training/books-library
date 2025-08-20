package com.library.catalog.aop;

import lombok.Getter;

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

    public static DuplicateCopyNumberException forCreation(Long bookId, String copyNumber) {
        String message = String.format("Cannot create book copy: copy number '%s' already exists for book ID: %s", 
                                      copyNumber, bookId);
        return new DuplicateCopyNumberException(bookId, copyNumber, message);
    }

    public static DuplicateCopyNumberException forUpdate(Long bookId, String copyNumber, Long copyId) {
        String message = String.format("Cannot update book copy ID %s: copy number '%s' already exists for book ID: %s", 
                                      copyId, copyNumber, bookId);
        return new DuplicateCopyNumberException(bookId, copyNumber, message);
    }

    public static DuplicateCopyNumberException fromDatabaseConstraint(Long bookId, String copyNumber, Throwable cause) {
        String message = String.format("Database constraint violation: copy number '%s' already exists for book ID: %s", 
                                      copyNumber, bookId);
        return new DuplicateCopyNumberException(bookId, copyNumber, message, cause);
    }
}