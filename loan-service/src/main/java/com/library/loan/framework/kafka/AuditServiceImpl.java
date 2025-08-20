package com.library.loan.framework.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.loan.framework.kafka.AuditEventBuilder;
import com.library.loan.framework.kafka.AuditEventMessage;
import com.library.loan.framework.kafka.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditEventPublisher auditEventPublisher;
    private final ObjectMapper objectMapper;

    @Override
    public void publishCreateEvent(String entityType, String entityId, Object newValue, String userId) {
        publishEvent(EventType.CREATED, entityType, entityId, null, newValue, userId);
    }

    @Override
    public void publishUpdateEvent(String entityType, String entityId, Object oldValue, Object newValue, String userId) {
        publishEvent(EventType.UPDATED, entityType, entityId, oldValue, newValue, userId);
    }

    @Override
    public void publishDeleteEvent(String entityType, String entityId, Object oldValue, String userId) {
        publishEvent(EventType.DELETED, entityType, entityId, oldValue, null, userId);
    }

    @Override
    public void publishAccessEvent(String entityType, String entityId, String userId, String details) {
        try {
            log.debug("Publishing access event for {} with ID: {}", entityType, entityId);

            AuditEventMessage event = AuditEventBuilder.builder()
                    .eventType(EventType.ACCESSED)
                    .entityType(entityType)
                    .entityId(entityId)
                    .userId(userId)
                    .changes(details)
                    .correlationId(MDC.get("correlationId"))
                    .build(objectMapper);

            auditEventPublisher.publishEvent(event);

            log.debug("Successfully published access event for {} with ID: {}", entityType, entityId);

        } catch (Exception e) {
            log.error("Failed to publish access event for {} with ID {}: {}", 
                     entityType, entityId, e.getMessage(), e);
            // Don't throw exception to avoid breaking the main business flow
        }
    }

    @Override
    public void publishEvent(EventType eventType, String entityType, String entityId, 
                           Object oldValue, Object newValue, String userId) {
        try {
            log.debug("Publishing {} event for {} with ID: {}", eventType, entityType, entityId);

            AuditEventMessage event = AuditEventBuilder.builder()
                    .eventType(eventType)
                    .entityType(entityType)
                    .entityId(entityId)
                    .oldValue(oldValue)
                    .newValue(newValue)
                    .userId(userId)
                    .correlationId(MDC.get("correlationId"))
                    .build(objectMapper);

            auditEventPublisher.publishEvent(event);

            log.debug("Successfully published {} event for {} with ID: {}", eventType, entityType, entityId);

        } catch (Exception e) {
            log.error("Failed to publish {} event for {} with ID {}: {}", 
                     eventType, entityType, entityId, e.getMessage(), e);
            // Don't throw exception to avoid breaking the main business flow
        }
    }
}