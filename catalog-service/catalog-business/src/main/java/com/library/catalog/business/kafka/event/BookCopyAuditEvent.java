package com.library.catalog.business.kafka.event;

import com.library.catalog.repository.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Audit event class for BookCopy operations.
 * Captures detailed change information for book copy operations including
 * status transitions, copy number changes, and location tracking.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookCopyAuditEvent {

    private String eventId;
    private EventType eventType;
    private String entityId;
    private Long bookId;
    private String userId;
    private LocalDateTime timestamp;
    
    // Change tracking fields
    private String oldCopyNumber;
    private String newCopyNumber;
    private BookCopyStatus oldStatus;
    private BookCopyStatus newStatus;
    private String oldLocation;
    private String newLocation;
    
    // Additional context
    private String actionDescription;
    private Map<String, Object> additionalData;

    /**
     * Creates a BookCopyAuditEvent for creation operations.
     */
    public static BookCopyAuditEvent forCreate(String entityId, Long bookId, String copyNumber, 
                                             BookCopyStatus status, String location, String userId) {
        return BookCopyAuditEvent.builder()
                .eventId(generateEventId())
                .eventType(EventType.CREATED)
                .entityId(entityId)
                .bookId(bookId)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .newCopyNumber(copyNumber)
                .newStatus(status)
                .newLocation(location)
                .actionDescription("Book copy created")
                .additionalData(new HashMap<>())
                .build();
    }

    /**
     * Creates a BookCopyAuditEvent for update operations with change tracking.
     */
    public static BookCopyAuditEvent forUpdate(String entityId, Long bookId, String userId,
                                             String oldCopyNumber, String newCopyNumber,
                                             BookCopyStatus oldStatus, BookCopyStatus newStatus,
                                             String oldLocation, String newLocation) {
        BookCopyAuditEventBuilder builder = BookCopyAuditEvent.builder()
                .eventId(generateEventId())
                .eventType(EventType.UPDATED)
                .entityId(entityId)
                .bookId(bookId)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .oldCopyNumber(oldCopyNumber)
                .newCopyNumber(newCopyNumber)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .oldLocation(oldLocation)
                .newLocation(newLocation)
                .additionalData(new HashMap<>());

        // Build action description based on what changed
        StringBuilder actionDesc = new StringBuilder("Book copy updated");
        if (!oldCopyNumber.equals(newCopyNumber)) {
            actionDesc.append(" - copy number changed");
        }
        if (!oldStatus.equals(newStatus)) {
            actionDesc.append(" - status changed");
        }
        if (!java.util.Objects.equals(oldLocation, newLocation)) {
            actionDesc.append(" - location changed");
        }

        return builder.actionDescription(actionDesc.toString()).build();
    }

    /**
     * Creates a BookCopyAuditEvent for deletion operations.
     */
    public static BookCopyAuditEvent forDelete(String entityId, Long bookId, String copyNumber,
                                             BookCopyStatus status, String location, String userId) {
        return BookCopyAuditEvent.builder()
                .eventId(generateEventId())
                .eventType(EventType.DELETED)
                .entityId(entityId)
                .bookId(bookId)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .oldCopyNumber(copyNumber)
                .oldStatus(status)
                .oldLocation(location)
                .actionDescription("Book copy deleted")
                .additionalData(new HashMap<>())
                .build();
    }

    /**
     * Checks if the copy number changed.
     */
    public boolean hasCopyNumberChange() {
        return oldCopyNumber != null && newCopyNumber != null && 
               !oldCopyNumber.equals(newCopyNumber);
    }

    /**
     * Checks if the status changed.
     */
    public boolean hasStatusChange() {
        return oldStatus != null && newStatus != null && 
               !oldStatus.equals(newStatus);
    }

    /**
     * Checks if the location changed.
     */
    public boolean hasLocationChange() {
        return !java.util.Objects.equals(oldLocation, newLocation);
    }

    /**
     * Gets a summary of all changes made.
     */
    public Map<String, Object> getChangesSummary() {
        Map<String, Object> changes = new HashMap<>();
        
        if (hasCopyNumberChange()) {
            Map<String, String> copyNumberChange = new HashMap<>();
            copyNumberChange.put("from", oldCopyNumber);
            copyNumberChange.put("to", newCopyNumber);
            changes.put("copyNumber", copyNumberChange);
        }
        
        if (hasStatusChange()) {
            Map<String, String> statusChange = new HashMap<>();
            statusChange.put("from", oldStatus.toString());
            statusChange.put("to", newStatus.toString());
            changes.put("status", statusChange);
        }
        
        if (hasLocationChange()) {
            Map<String, String> locationChange = new HashMap<>();
            locationChange.put("from", oldLocation);
            locationChange.put("to", newLocation);
            changes.put("location", locationChange);
        }
        
        return changes;
    }

    /**
     * Adds additional context data to the audit event.
     */
    public void addAdditionalData(String key, Object value) {
        if (additionalData == null) {
            additionalData = new HashMap<>();
        }
        additionalData.put(key, value);
    }

    /**
     * Generates a unique event ID.
     */
    private static String generateEventId() {
        return "book-copy-" + System.currentTimeMillis() + "-" + 
               java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}