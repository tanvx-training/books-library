package com.library.notification.dto.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public abstract class LibraryEvent {
    
    private String eventId;
    private String eventType;
    private UUID userPublicId;
    private LocalDateTime timestamp;
    private String source;
}