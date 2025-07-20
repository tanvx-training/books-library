package com.library.catalog.business.kafka.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Builder class for creating AuditEventMessage instances.
 * Provides a fluent API for constructing audit events with proper validation and defaults.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditEventBuilder {

    private final ObjectMapper objectMapper;

    /**
     * Creates a new builder instance for constructing audit events.
     *
     * @return A new AuditEventBuilder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing AuditEventMessage instances.
     */
    public static class Builder {
        private String eventId;
        private EventType eventType;
        private String serviceName;
        private String entityType;
        private String entityId;
        private String userId;
        private String userInfo;
        private Object oldValue;
        private Object newValue;
        private String changes;
        private LocalDateTime timestamp;

        private Builder() {
            this.eventId = UUID.randomUUID().toString();
            this.timestamp = LocalDateTime.now();
            this.serviceName = "catalog-service";
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder entityType(String entityType) {
            this.entityType = entityType;
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder userInfo(String userInfo) {
            this.userInfo = userInfo;
            return this;
        }

        public Builder oldValue(Object oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        public Builder newValue(Object newValue) {
            this.newValue = newValue;
            return this;
        }

        public Builder changes(String changes) {
            this.changes = changes;
            return this;
        }

        /**
         * Builds the AuditEventMessage with validation.
         *
         * @param objectMapper ObjectMapper for serializing values
         * @return Constructed AuditEventMessage
         * @throws IllegalArgumentException if required fields are missing
         */
        public AuditEventMessage build(ObjectMapper objectMapper) {
            validateRequiredFields();

            AuditEventMessage event = new AuditEventMessage();
            event.setEventId(eventId);
            event.setEventType(eventType.getValue());
            event.setServiceName(serviceName);
            event.setEntityType(entityType);
            event.setEntityId(entityId);
            event.setUserId(userId);
            event.setUserInfo(userInfo);
            event.setTimestamp(timestamp);
            event.setChanges(changes);

            // Serialize old and new values to JSON strings
            if (oldValue != null) {
                event.setOldValue(serializeToJson(oldValue, objectMapper));
            }
            if (newValue != null) {
                event.setNewValue(serializeToJson(newValue, objectMapper));
            }

            return event;
        }

        private void validateRequiredFields() {
            if (eventType == null) {
                throw new IllegalArgumentException("Event type is required");
            }
            if (entityType == null || entityType.trim().isEmpty()) {
                throw new IllegalArgumentException("Entity type is required");
            }
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("Entity ID is required");
            }
        }

        private String serializeToJson(Object value, ObjectMapper objectMapper) {
            try {
                return objectMapper.writeValueAsString(value);
            } catch (JsonProcessingException e) {
                log.warn("Failed to serialize value to JSON: {}", e.getMessage());
                return value.toString();
            }
        }
    }
}