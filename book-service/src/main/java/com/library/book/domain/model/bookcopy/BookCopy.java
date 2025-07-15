package com.library.book.domain.model.bookcopy;

import com.library.book.domain.event.BookCopyCreatedEvent;
import com.library.book.domain.event.BookCopyStatusChangedEvent;
import com.library.book.domain.exception.InvalidBookCopyOperationException;
import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * BookCopy Aggregate Root
 * Represents a physical copy of a book in the library
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookCopy extends AggregateRoot {

    private BookCopyId id;
    private BookId bookId;
    private CopyNumber copyNumber;
    private BookCopyStatus status;
    private BookCondition condition;
    private Location location;
    private LocalDateTime acquiredDate;
    private String currentBorrowerKeycloakId; // User who currently has this copy
    private LocalDateTime borrowedDate;
    private LocalDateTime dueDate;
    private boolean deleted;

    /**
     * Factory method to create a new book copy
     */
    public static BookCopy create(
            BookId bookId,
            CopyNumber copyNumber,
            BookCondition condition,
            Location location) {
        
        BookCopy bookCopy = new BookCopy();
        bookCopy.id = BookCopyId.createNew();
        bookCopy.bookId = bookId;
        bookCopy.copyNumber = copyNumber;
        bookCopy.status = BookCopyStatus.AVAILABLE;
        bookCopy.condition = condition;
        bookCopy.location = location;
        bookCopy.acquiredDate = LocalDateTime.now();
        bookCopy.deleted = false;

        bookCopy.registerEvent(new BookCopyCreatedEvent(
            bookCopy.id.getValue(),
            bookCopy.bookId.getValue(),
            bookCopy.copyNumber.getValue()
        ));

        return bookCopy;
    }

    /**
     * Business method: Borrow this copy
     */
    public void borrowTo(String borrowerKeycloakId, LocalDateTime dueDate) {
        if (!canBeBorrowed()) {
            throw new InvalidBookCopyOperationException(
                "Book copy cannot be borrowed. Current status: " + status);
        }

        if (borrowerKeycloakId == null || borrowerKeycloakId.trim().isEmpty()) {
            throw new IllegalArgumentException("Borrower Keycloak ID cannot be null or empty");
        }

        if (dueDate == null || dueDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date must be in the future");
        }

        BookCopyStatus previousStatus = this.status;
        this.status = BookCopyStatus.BORROWED;
        this.currentBorrowerKeycloakId = borrowerKeycloakId;
        this.borrowedDate = LocalDateTime.now();
        this.dueDate = dueDate;

        registerEvent(new BookCopyStatusChangedEvent(
            this.id.getValue(),
            previousStatus,
            this.status,
            borrowerKeycloakId
        ));
    }

    /**
     * Business method: Return this copy
     */
    public void returnCopy() {
        if (!canBeReturned()) {
            throw new InvalidBookCopyOperationException(
                "Book copy cannot be returned. Current status: " + status);
        }

        BookCopyStatus previousStatus = this.status;
        String previousBorrower = this.currentBorrowerKeycloakId;
        
        this.status = BookCopyStatus.AVAILABLE;
        this.currentBorrowerKeycloakId = null;
        this.borrowedDate = null;
        this.dueDate = null;

        registerEvent(new BookCopyStatusChangedEvent(
            this.id.getValue(),
            previousStatus,
            this.status,
            previousBorrower
        ));
    }

    /**
     * Business method: Reserve this copy
     */
    public void reserve(String reserverKeycloakId) {
        if (!canBeReserved()) {
            throw new InvalidBookCopyOperationException(
                "Book copy cannot be reserved. Current status: " + status);
        }

        if (reserverKeycloakId == null || reserverKeycloakId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reserver Keycloak ID cannot be null or empty");
        }

        BookCopyStatus previousStatus = this.status;
        this.status = BookCopyStatus.RESERVED;
        this.currentBorrowerKeycloakId = reserverKeycloakId; // Temporarily store reserver

        registerEvent(new BookCopyStatusChangedEvent(
            this.id.getValue(),
            previousStatus,
            this.status,
            reserverKeycloakId
        ));
    }

    /**
     * Business method: Mark as lost
     */
    public void markAsLost() {
        BookCopyStatus previousStatus = this.status;
        String previousBorrower = this.currentBorrowerKeycloakId;
        
        this.status = BookCopyStatus.LOST;
        this.currentBorrowerKeycloakId = null;
        this.borrowedDate = null;
        this.dueDate = null;

        registerEvent(new BookCopyStatusChangedEvent(
            this.id.getValue(),
            previousStatus,
            this.status,
            previousBorrower
        ));
    }

    /**
     * Business method: Mark for maintenance
     */
    public void markForMaintenance() {
        if (status == BookCopyStatus.BORROWED) {
            throw new InvalidBookCopyOperationException(
                "Cannot mark borrowed book for maintenance");
        }

        BookCopyStatus previousStatus = this.status;
        this.status = BookCopyStatus.MAINTENANCE;

        registerEvent(new BookCopyStatusChangedEvent(
            this.id.getValue(),
            previousStatus,
            this.status,
            null
        ));
    }

    /**
     * Business method: Update condition
     */
    public void updateCondition(BookCondition newCondition) {
        if (newCondition == null) {
            throw new IllegalArgumentException("Book condition cannot be null");
        }

        this.condition = newCondition;

        // If condition is poor, automatically mark for maintenance
        if (newCondition == BookCondition.POOR && status != BookCopyStatus.MAINTENANCE) {
            markForMaintenance();
        }
    }

    /**
     * Business method: Update location
     */
    public void updateLocation(Location newLocation) {
        if (newLocation == null) {
            throw new IllegalArgumentException("Location cannot be null");
        }
        this.location = newLocation;
    }

    /**
     * Business method: Mark as deleted (soft delete)
     */
    public void markAsDeleted() {
        if (status == BookCopyStatus.BORROWED) {
            throw new InvalidBookCopyOperationException(
                "Cannot delete borrowed book copy");
        }
        this.deleted = true;
    }

    // Business rules
    public boolean canBeBorrowed() {
        return status.isAvailableForBorrowing() && 
               condition.isAcceptableForLending() && 
               !deleted;
    }

    public boolean canBeReturned() {
        return status.canBeReturned();
    }

    public boolean canBeReserved() {
        return status.isAvailableForReservation() && !deleted;
    }

    public boolean isOverdue() {
        return status == BookCopyStatus.BORROWED && 
               dueDate != null && 
               LocalDateTime.now().isAfter(dueDate);
    }

    public boolean isBorrowedBy(String keycloakId) {
        return status == BookCopyStatus.BORROWED && 
               keycloakId != null && 
               keycloakId.equals(currentBorrowerKeycloakId);
    }

    public boolean isReservedBy(String keycloakId) {
        return status == BookCopyStatus.RESERVED && 
               keycloakId != null && 
               keycloakId.equals(currentBorrowerKeycloakId);
    }

    // For JPA/ORM reconstruction
    public static BookCopy reconstitute(
            BookCopyId id,
            BookId bookId,
            CopyNumber copyNumber,
            BookCopyStatus status,
            BookCondition condition,
            Location location,
            LocalDateTime acquiredDate,
            String currentBorrowerKeycloakId,
            LocalDateTime borrowedDate,
            LocalDateTime dueDate,
            boolean deleted) {
        
        BookCopy bookCopy = new BookCopy();
        bookCopy.id = id;
        bookCopy.bookId = bookId;
        bookCopy.copyNumber = copyNumber;
        bookCopy.status = status;
        bookCopy.condition = condition;
        bookCopy.location = location;
        bookCopy.acquiredDate = acquiredDate;
        bookCopy.currentBorrowerKeycloakId = currentBorrowerKeycloakId;
        bookCopy.borrowedDate = borrowedDate;
        bookCopy.dueDate = dueDate;
        bookCopy.deleted = deleted;
        
        return bookCopy;
    }
}