package com.library.loan.business.kafka.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditEventBuilder {

    private final ObjectMapper objectMapper;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final String eventId;
        private EventType eventType;
        private final String serviceName;
        private String entityType;
        private String entityId;
        private String userId;
        private String userInfo;
        private Object oldValue;
        private Object newValue;
        private String changes;
        private final LocalDateTime timestamp;
        private String correlationId;
        private String sessionId;
        private String ipAddress;
        private String userAgent;

        private Builder() {
            this.eventId = UUID.randomUUID().toString();
            this.timestamp = LocalDateTime.now();
            this.serviceName = "loan-service";
            
            // Extract correlation ID from MDC if available
            this.correlationId = MDC.get("correlationId");
            this.sessionId = MDC.get("sessionId");
            this.ipAddress = MDC.get("ipAddress");
            this.userAgent = MDC.get("userAgent");
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

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

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
            event.setCorrelationId(correlationId);
            event.setSessionId(sessionId);
            event.setIpAddress(ipAddress);
            event.setUserAgent(userAgent);

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