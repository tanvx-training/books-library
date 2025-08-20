package com.library.loan.aop;

import lombok.Getter;

@Getter
public class BorrowingBusinessException extends RuntimeException {

    private final String borrowingId;
    private final String businessRule;
    private final String operation;

    public BorrowingBusinessException(String message) {
        super(message);
        this.borrowingId = null;
        this.businessRule = null;
        this.operation = null;
    }

    public BorrowingBusinessException(String message, Throwable cause) {
        super(message, cause);
        this.borrowingId = null;
        this.businessRule = null;
        this.operation = null;
    }

    public BorrowingBusinessException(String borrowingId, String operation, String businessRule, String message) {
        super(message);
        this.borrowingId = borrowingId;
        this.operation = operation;
        this.businessRule = businessRule;
    }

    public static BorrowingBusinessException bookNotAvailable(String bookCopyId) {
        String message = String.format("Book copy with ID %s is not available for borrowing", bookCopyId);
        return new BorrowingBusinessException(null, "create", "BOOK_AVAILABILITY", message);
    }

    public static BorrowingBusinessException borrowingLimitExceeded(String userId, int currentCount, int maxLimit) {
        String message = String.format("User %s has reached maximum borrowing limit (%d/%d)", userId, currentCount, maxLimit);
        return new BorrowingBusinessException(null, "create", "BORROWING_LIMIT", message);
    }

    public static BorrowingBusinessException alreadyReturned(String borrowingId) {
        String message = "Book is already returned and cannot be returned again";
        return new BorrowingBusinessException(borrowingId, "return", "ALREADY_RETURNED", message);
    }

    public static BorrowingBusinessException cannotReturnInactiveStatus(String borrowingId, String currentStatus) {
        String message = String.format("Cannot return borrowing with status %s. Only active borrowings can be returned", currentStatus);
        return new BorrowingBusinessException(borrowingId, "return", "INVALID_STATUS", message);
    }

    public static BorrowingBusinessException cannotRenewOverdue(String borrowingId) {
        String message = "Overdue borrowings cannot be renewed. Please return the book and pay any applicable fines";
        return new BorrowingBusinessException(borrowingId, "renew", "OVERDUE_RENEWAL", message);
    }

    public static BorrowingBusinessException cannotRenewInactiveStatus(String borrowingId, String currentStatus) {
        String message = String.format("Cannot renew borrowing with status %s. Only active borrowings can be renewed", currentStatus);
        return new BorrowingBusinessException(borrowingId, "renew", "INVALID_STATUS", message);
    }

    public static BorrowingBusinessException renewalLimitExceeded(String borrowingId, int currentRenewals, int maxRenewals) {
        String message = String.format("Borrowing has reached maximum renewal limit (%d/%d)", currentRenewals, maxRenewals);
        return new BorrowingBusinessException(borrowingId, "renew", "RENEWAL_LIMIT", message);
    }

    public static BorrowingBusinessException invalidDueDate(String borrowingId, String operation, String reason) {
        String message = String.format("Invalid due date for %s operation: %s", operation, reason);
        return new BorrowingBusinessException(borrowingId, operation, "INVALID_DUE_DATE", message);
    }

    public static BorrowingBusinessException userNotEligible(String userId, String reason) {
        String message = String.format("User %s is not eligible to borrow books: %s", userId, reason);
        return new BorrowingBusinessException(null, "create", "USER_ELIGIBILITY", message);
    }

    public static BorrowingBusinessException outstandingFines(String userId, String fineAmount) {
        String message = String.format("User %s has outstanding fines of %s and cannot borrow additional books", userId, fineAmount);
        return new BorrowingBusinessException(null, "create", "OUTSTANDING_FINES", message);
    }

    public static BorrowingBusinessException bookCopyNotFound(String bookCopyId) {
        String message = String.format("Book copy with ID %s not found or is not available", bookCopyId);
        return new BorrowingBusinessException(null, "create", "BOOK_COPY_NOT_FOUND", message);
    }

    public static BorrowingBusinessException userNotFound(String userId) {
        String message = String.format("User with ID %s not found or is not active", userId);
        return new BorrowingBusinessException(null, "create", "USER_NOT_FOUND", message);
    }
}