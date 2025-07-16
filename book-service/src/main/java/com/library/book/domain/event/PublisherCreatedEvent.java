package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PublisherCreatedEvent extends AuditEvent {
    private final Long publisherId;
    private final String publisherName;

    public PublisherCreatedEvent(Long publisherId, String publisherName) {
        super("PublisherCreated", LocalDateTime.now());
        this.publisherId = publisherId;
        this.publisherName = publisherName;
    }
    
    public PublisherCreatedEvent() {
        super("PublisherCreated", LocalDateTime.now());
        this.publisherId = null;
        this.publisherName = null;
    }

    public String getAggregateId() {
        return publisherId != null ? publisherId.toString() : "unknown";
    }

    public String getEventData() {
        return String.format("Publisher created - ID: %s, Name: %s", publisherId, publisherName);
    }
}