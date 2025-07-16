package com.library.book.domain.event;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CategoryCreatedEvent extends AuditEvent {
    private final Long categoryId;
    private final String categoryName;

    public CategoryCreatedEvent(Long categoryId, String categoryName) {
        super("CategoryCreated", LocalDateTime.now());
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }
    
    public CategoryCreatedEvent() {
        super("CategoryCreated", LocalDateTime.now());
        this.categoryId = null;
        this.categoryName = null;
    }

    public String getAggregateId() {
        return categoryId != null ? categoryId.toString() : "unknown";
    }

    public String getEventData() {
        return String.format("Category created - ID: %s, Name: %s", categoryId, categoryName);
    }
}