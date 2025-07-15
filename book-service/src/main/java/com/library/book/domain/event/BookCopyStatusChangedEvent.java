package com.library.book.domain.event;

import com.library.book.domain.model.bookcopy.BookCopyStatus;
import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BookCopyStatusChangedEvent extends AuditEvent {
    private final Long bookCopyId;
    private final BookCopyStatus previousStatus;
    private final BookCopyStatus newStatus;
    private final String userKeycloakId;

    public BookCopyStatusChangedEvent(
            Long bookCopyId, 
            BookCopyStatus previousStatus, 
            BookCopyStatus newStatus,
            String userKeycloakId) {
        super("BookCopyStatusChanged", LocalDateTime.now());
        this.bookCopyId = bookCopyId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.userKeycloakId = userKeycloakId;
    }

    @Override
    public String getAggregateId() {
        return bookCopyId.toString();
    }

    @Override
    public String getEventData() {
        return String.format("BookCopy status changed - ID: %d, From: %s, To: %s, User: %s", 
            bookCopyId, previousStatus, newStatus, userKeycloakId);
    }
}