package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookCopyCreatedEvent extends AuditEvent {
    private final Long bookCopyId;
    private final Long bookId;
    private final String copyNumber;

    public BookCopyCreatedEvent(Long bookCopyId, Long bookId, String copyNumber) {
        super("BookCopyCreated", LocalDateTime.now());
        this.bookCopyId = bookCopyId;
        this.bookId = bookId;
        this.copyNumber = copyNumber;
    }

    public String getAggregateId() {
        return bookCopyId.toString();
    }

    public String getEventData() {
        return String.format("BookCopy created - ID: %d, BookID: %d, CopyNumber: %s", 
            bookCopyId, bookId, copyNumber);
    }
}