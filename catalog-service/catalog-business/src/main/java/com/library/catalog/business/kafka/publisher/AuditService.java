package com.library.catalog.business.kafka.publisher;

import com.library.catalog.business.kafka.event.EventType;

/**
 * Service interface for handling audit operations.
 * This service provides methods for publishing audit events for various entity operations.
 */
public interface AuditService {

    /**
     * Publishes an audit event for entity creation.
     *
     * @param entityType The type of entity (e.g., "Author", "Book")
     * @param entityId The ID of the created entity
     * @param newValue The created entity data
     * @param userId The ID of the user who performed the action
     */
    void publishCreateEvent(String entityType, String entityId, Object newValue, String userId);

    /**
     * Publishes an audit event for entity update.
     *
     * @param entityType The type of entity (e.g., "Author", "Book")
     * @param entityId The ID of the updated entity
     * @param oldValue The entity data before update
     * @param newValue The entity data after update
     * @param userId The ID of the user who performed the action
     */
    void publishUpdateEvent(String entityType, String entityId, Object oldValue, Object newValue, String userId);

    /**
     * Publishes an audit event for entity deletion.
     *
     * @param entityType The type of entity (e.g., "Author", "Book")
     * @param entityId The ID of the deleted entity
     * @param oldValue The entity data before deletion
     * @param userId The ID of the user who performed the action
     */
    void publishDeleteEvent(String entityType, String entityId, Object oldValue, String userId);

    /**
     * Publishes a generic audit event.
     *
     * @param eventType The type of event
     * @param entityType The type of entity
     * @param entityId The ID of the entity
     * @param oldValue The entity data before the operation (can be null)
     * @param newValue The entity data after the operation (can be null)
     * @param userId The ID of the user who performed the action
     */
    void publishEvent(EventType eventType, String entityType, String entityId, 
                     Object oldValue, Object newValue, String userId);
}