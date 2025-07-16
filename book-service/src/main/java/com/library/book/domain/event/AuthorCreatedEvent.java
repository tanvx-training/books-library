package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AuthorCreatedEvent extends AuditEvent {
    private final Long authorId;
    private final String authorName;

    public AuthorCreatedEvent(Long authorId, String authorName) {
        super("AuthorCreated", LocalDateTime.now());
        this.authorId = authorId;
        this.authorName = authorName;
    }
    
    public AuthorCreatedEvent() {
        super("AuthorCreated", LocalDateTime.now());
        this.authorId = null;
        this.authorName = null;
    }

    public String getAggregateId() {
        return authorId != null ? authorId.toString() : "unknown";
    }

    public String getEventData() {
        return String.format("Author created - ID: %s, Name: %s", authorId, authorName);
    }
}
