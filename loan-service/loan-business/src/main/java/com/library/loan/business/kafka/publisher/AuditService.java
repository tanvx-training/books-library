package com.library.loan.business.kafka.publisher;

import com.library.loan.business.kafka.event.EventType;

/**
 * Service interface for audit logging and event publishing.
 * Provides high-level methods for publishing different types of audit events.
 */
public interface AuditService {

    /**
     * Publishes a create event for an entity.
     * @param entityType the type of entity (e.g., "BORROWING")
     * @param entityId the unique identifier of the entity
     * @param newValue the new entity value
     * @param userId the ID of the user performing the action
     */
    void publishCreateEvent(String entityType, String entityId, Object newValue, String userId);

    /**
     * Publishes an update event for an entity.
     * @param entityType the type of entity (e.g., "BORROWING")
     * @param entityId the unique identifier of the entity
     * @param oldValue the old entity value
     * @param newValue the new entity value
     * @param userId the ID of the user performing the action
     */
    void publishUpdateEvent(String entityType, String entityId, Object oldValue, Object newValue, String userId);

    /**
     * Publishes a delete event for an entity.
     * @param entityType the type of entity (e.g., "BORROWING")
     * @param entityId the unique identifier of the entity
     * @param oldValue the old entity value
     * @param userId the ID of the user performing the action
     */
    void publishDeleteEvent(String entityType, String entityId, Object oldValue, String userId);

    /**
     * Publishes a custom event for an entity.
     * @param eventType the type of event
     * @param entityType the type of entity (e.g., "BORROWING")
     * @param entityId the unique identifier of the entity
     * @param oldValue the old entity value (can be null)
     * @param newValue the new entity value (can be null)
     * @param userId the ID of the user performing the action
     */
    void publishEvent(EventType eventType, String entityType, String entityId, 
                     Object oldValue, Object newValue, String userId);

    /**
     * Publishes an access event for an entity.
     * @param entityType the type of entity (e.g., "BORROWING")
     * @param entityId the unique identifier of the entity
     * @param userId the ID of the user performing the action
     * @param details additional details about the access
     */
    void publishAccessEvent(String entityType, String entityId, String userId, String details);
}