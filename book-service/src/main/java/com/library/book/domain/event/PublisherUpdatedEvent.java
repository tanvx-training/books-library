package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PublisherUpdatedEvent extends AuditEvent {
    private final Long publisherId;
    private final String fieldName;
    private final String newValue;

    public PublisherUpdatedEvent(Long publisherId, String fieldName, String newValue) {
        super("PublisherUpdated", LocalDateTime.now());
        this.publisherId = publisherId;
        this.fieldName = fieldName;
        this.newValue = newValue;
    }

    public String getAggregateId() {
        return publisherId.toString();
    }

    public String getEventData() {
        return String.format("Publisher updated - ID: %d, Field: %s, New Value: %s", 
            publisherId, fieldName, newValue);
    }
}