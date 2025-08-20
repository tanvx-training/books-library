package com.library.catalog.aop;

import com.library.catalog.repository.BookCopyStatus;
import lombok.Getter;

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

    public static InvalidStatusTransitionException forUpdate(Long copyId, BookCopyStatus fromStatus, BookCopyStatus toStatus) {
        String message = String.format("Invalid status transition for book copy ID %s: cannot change from %s to %s", 
                                      copyId, fromStatus, toStatus);
        return new InvalidStatusTransitionException(copyId, fromStatus, toStatus, message);
    }

    public static InvalidStatusTransitionException forDeletion(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot delete book copy ID %s: status %s is not allowed for deletion. " +
                                      "Only AVAILABLE, LOST, or DAMAGED copies can be deleted", copyId, status);
        return new InvalidStatusTransitionException(copyId, status, null, message);
    }

    public static InvalidStatusTransitionException forBorrowing(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot borrow book copy ID %s: current status is %s, must be AVAILABLE", 
                                      copyId, status);
        return new InvalidStatusTransitionException(copyId, status, BookCopyStatus.BORROWED, message);
    }

    public static InvalidStatusTransitionException forReservation(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot reserve book copy ID %s: current status is %s, must be AVAILABLE", 
                                      copyId, status);
        return new InvalidStatusTransitionException(copyId, status, BookCopyStatus.RESERVED, message);
    }

    public static InvalidStatusTransitionException forReturn(Long copyId, BookCopyStatus status) {
        String message = String.format("Cannot return book copy ID %s: current status is %s, must be BORROWED", 
                                      copyId, status);
        return new InvalidStatusTransitionException(copyId, status, BookCopyStatus.AVAILABLE, message);
    }
}