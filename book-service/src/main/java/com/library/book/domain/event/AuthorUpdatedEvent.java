package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuthorUpdatedEvent extends AuditEvent {
    private final Long authorId;
    private final String fieldName;
    private final String newValue;

    public AuthorUpdatedEvent(Long authorId, String fieldName, String newValue) {
        super("AuthorUpdated", LocalDateTime.now());
        this.authorId = authorId;
        this.fieldName = fieldName;
        this.newValue = newValue;
    }

    public String getAggregateId() {
        return authorId.toString();
    }

    public String getEventData() {
        return String.format("Author updated - ID: %d, Field: %s, New Value: %s", 
            authorId, fieldName, newValue);
    }
}