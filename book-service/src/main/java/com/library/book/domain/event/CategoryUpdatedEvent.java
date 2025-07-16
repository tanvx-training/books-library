package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CategoryUpdatedEvent extends AuditEvent {
    private final Long categoryId;
    private final String fieldName;
    private final String newValue;

    public CategoryUpdatedEvent(Long categoryId, String fieldName, String newValue) {
        super("CategoryUpdated", LocalDateTime.now());
        this.categoryId = categoryId;
        this.fieldName = fieldName;
        this.newValue = newValue;
    }

    public String getAggregateId() {
        return categoryId.toString();
    }

    public String getEventData() {
        return String.format("Category updated - ID: %d, Field: %s, New Value: %s", 
            categoryId, fieldName, newValue);
    }
}