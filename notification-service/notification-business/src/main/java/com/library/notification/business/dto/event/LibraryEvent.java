package com.library.notification.business.dto.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for library events
 */
@Data
public abstract class LibraryEvent {
    
    private String eventId;
    private String eventType;
    private UUID userPublicId;
    private LocalDateTime timestamp;
    private String source;
}