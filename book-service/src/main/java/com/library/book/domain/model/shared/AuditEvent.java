package com.library.book.domain.model.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuditEvent {
    private String eventId;
    private String eventType;
    private String entityType;
    private String entityId;
    private String userId;
    private String userInfo;
    private String oldValue;
    private String newValue;
    private String changes;
    private String requestId;
    private LocalDateTime timestamp;
    
    public AuditEvent(String eventType, String entityType, String entityId) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = LocalDateTime.now();
    }
}