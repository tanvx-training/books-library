package com.library.book.domain.model.bookcopy;

import com.library.book.domain.event.BookCopyCreatedEvent;
import com.library.book.domain.event.BookCopyStatusChangedEvent;
import com.library.book.domain.exception.InvalidBookCopyDataException;
import com.library.book.domain.model.shared.AggregateRoot;
import lombok.Getter;

@Getter
public class BookCopy extends AggregateRoot {
    private BookCopyId id;
    private BookReference bookReference;
    private CopyNumber copyNumber;
    private BookCopyStatus status;
    private BookCopyCondition condition;
    private Location location;
    private boolean deleted;
    
    // Private constructor for factory methods
    private BookCopy() {}
    
    // Factory method for creating a new book copy
    public static BookCopy create(
            BookReference bookReference,
            CopyNumber copyNumber,
            BookCopyStatus status,
            BookCopyCondition condition,
            Location location) {
        
        BookCopy bookCopy = new BookCopy();
        bookCopy.id = BookCopyId.createNew();
        bookCopy.bookReference = bookReference;
        bookCopy.copyNumber = copyNumber;
        bookCopy.status = status != null ? status : BookCopyStatus.AVAILABLE;
        bookCopy.condition = condition;
        bookCopy.location = location != null ? location : Location.empty();
        bookCopy.deleted = false;
        
        bookCopy.registerEvent(new BookCopyCreatedEvent(bookCopy));
        
        return bookCopy;
    }
    
    // Method to update status
    public void updateStatus(BookCopyStatus newStatus) {
        if (newStatus == null) {
            throw new InvalidBookCopyDataException("status", "Status cannot be null");
        }
        
        validateStatusChange(this.status, newStatus);
        
        BookCopyStatus oldStatus = this.status;
        this.status = newStatus;
        
        registerEvent(new BookCopyStatusChangedEvent(this, oldStatus, newStatus));
    }
    
    // Method to update condition
    public void updateCondition(BookCopyCondition newCondition) {
        this.condition = newCondition;
    }
    
    // Method to update location
    public void updateLocation(Location newLocation) {
        this.location = newLocation != null ? newLocation : Location.empty();
    }
    
    // Method to update copy number
    public void updateCopyNumber(CopyNumber newCopyNumber) {
        this.copyNumber = newCopyNumber;
    }
    
    // Method to mark as deleted
    public void markAsDeleted() {
        if (status != BookCopyStatus.AVAILABLE) {
            throw new InvalidBookCopyDataException("status", 
                "Cannot delete book copy that is not in AVAILABLE status");
        }
        
        this.deleted = true;
    }
    
    // Helper method to validate status changes
    private void validateStatusChange(BookCopyStatus currentStatus, BookCopyStatus newStatus) {
        // Example business rule: Cannot change from LOST to AVAILABLE directly
        if (currentStatus == BookCopyStatus.LOST && newStatus == BookCopyStatus.AVAILABLE) {
            throw new InvalidBookCopyDataException("status", 
                "Cannot change status from LOST to AVAILABLE directly");
        }
        
        // Example business rule: Cannot change from BORROWED to LOST directly
        if (currentStatus == BookCopyStatus.BORROWED && newStatus == BookCopyStatus.LOST) {
            throw new InvalidBookCopyDataException("status",
                "Cannot change status from BORROWED to LOST directly");
        }
    }
    
    // For JPA/ORM reconstruction
    public static BookCopy reconstitute(
            BookCopyId id,
            BookReference bookReference,
            CopyNumber copyNumber,
            BookCopyStatus status,
            BookCopyCondition condition,
            Location location,
            boolean deleted) {
        
        BookCopy bookCopy = new BookCopy();
        bookCopy.id = id;
        bookCopy.bookReference = bookReference;
        bookCopy.copyNumber = copyNumber;
        bookCopy.status = status;
        bookCopy.condition = condition;
        bookCopy.location = location;
        bookCopy.deleted = deleted;
        
        return bookCopy;
    }
} 