package com.library.catalog.business.aop.exception;

import com.library.catalog.repository.enums.BookCopyStatus;
import lombok.Getter;

/**
 * Exception class for invalid status transition violations.
 * Extends BookCopyException to handle business rule violations for status changes.
 */
@Getter
public class InvalidStatusTransitionException extends BookCopyException {

    private final BookCopyStatus fromStatus;
    private final BookCopyStatus toStatus;
    private final Long copyId;

    public InvalidStatusTransitionException(Long copyId, BookCopyStatus fromStatus, BookCopyStatus toStatus, String message) {
        super("status transition", message);
        this.copyId = copyId;
        this.fromStatus = fromStatus;
        this.toStatus = toStatus;
    }

    /**
     * Creates an exception for invalid status transitions during updates.
     *
     * @param copyId     the book copy ID
     * @param fromStatus the current status
     * @param toStatus   the target status
     * @return InvalidStatusTransitionException with transition details
     */
    public static InvalidStatusTransitionException forUpdate(Long copyId, BookCopyStatus fromStatus, BookCopyStatus toStatus) {
        String message = String.format("Invalid status transition for book copy ID %s: cannot change from %s to %s", 
                                      copyId, fromStatus, toStatus);
        return new InvalidStatusTransitionException(copyId, fromStatus, toStatus, message);
    }

    /**
     * Creates an exception for attempting to delete a copy with invalid status.
     *
     * @param copyId the book copy ID
     * @param status the current status
     * @return InvalidStatusTransitionException with deletion context
     */
    public static InvalidStatusTransitionException forDeletion(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot delete book copy ID %s: status %s is not allowed for deletion. " +
                                      "Only AVAILABLE, LOST, or DAMAGED copies can be deleted", copyId, status);
        return new InvalidStatusTransitionException(copyId, status, null, message);
    }

    /**
     * Creates an exception for attempting to borrow a copy that's not available.
     *
     * @param copyId the book copy ID
     * @param status the current status
     * @return InvalidStatusTransitionException with borrowing context
     */
    public static InvalidStatusTransitionException forBorrowing(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot borrow book copy ID %s: current status is %s, must be AVAILABLE", 
                                      copyId, status);
        return new InvalidStatusTransitionException(copyId, status, BookCopyStatus.BORROWED, message);
    }

    /**
     * Creates an exception for attempting to reserve a copy that's not available.
     *
     * @param copyId the book copy ID
     * @param status the current status
     * @return InvalidStatusTransitionException with reservation context
     */
    public static InvalidStatusTransitionException forReservation(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot reserve book copy ID %s: current status is %s, must be AVAILABLE", 
                                      copyId, status);
        return new InvalidStatusTransitionException(copyId, status, BookCopyStatus.RESERVED, message);
    }

    /**
     * Creates an exception for attempting to return a copy that's not borrowed.
     *
     * @param copyId the book copy ID
     * @param status the current status
     * @return InvalidStatusTransitionException with return context
     */
    public static InvalidStatusTransitionException forReturn(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot return book copy ID %s: current status is %s, must be BORROWED", 
                                      copyId, status);
        return new InvalidStatusTransitionException(copyId, status, BookCopyStatus.AVAILABLE, message);
    }
}