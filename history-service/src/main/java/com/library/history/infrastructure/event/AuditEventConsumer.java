package com.library.history.infrastructure.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.history.application.dto.request.AuditLogCreateRequest;
import com.library.history.application.service.AuditLogApplicationService;
import com.library.history.domain.model.audit_log.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditLogApplicationService auditLogApplicationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {"book-service-events", "user-service-events", "lending-service-events"})
    public void consumeAuditEvent(String message) {
        try {
            log.info("Received audit event: {}", message);
            
            Map eventMap = objectMapper.readValue(message, Map.class);
            String eventType = (String) eventMap.get("eventType");
            String serviceName = extractServiceName(eventType);
            
            if (eventMap.containsKey("payload")) {
                Object payload = eventMap.get("payload");
                String entityName = extractEntityName(eventType);
                String entityId = extractEntityId(payload);
                String actionType = extractActionType(eventType);
                
                AuditLogCreateRequest request = AuditLogCreateRequest.builder()
                        .serviceName(serviceName)
                        .entityName(entityName)
                        .entityId(entityId)
                        .actionType(actionType)
                        .userId(extractUserId(eventMap))
                        .userInfo(extractUserInfo(eventMap))
                        .oldValue(extractOldValue(payload, actionType))
                        .newValue(extractNewValue(payload, actionType))
                        .changes(extractChanges(payload, actionType))
                        .requestId(extractRequestId(eventMap))
                        .build();
                
                auditLogApplicationService.createAuditLog(request);
                log.info("Successfully processed audit event for {}: {}", entityName, entityId);
            }
        } catch (Exception e) {
            log.error("Error processing audit event: {}", message, e);
        }
    }

    private String extractServiceName(String eventType) {
        if (eventType.startsWith("BOOK_")) {
            return "book-service";
        } else if (eventType.startsWith("USER_")) {
            return "user-service";
        } else if (eventType.startsWith("LENDING_")) {
            return "lending-service";
        }
        return "unknown-service";
    }

    private String extractEntityName(String eventType) {
        // Extract entity name from event type (e.g., BOOK_CREATED -> Book)
        String[] parts = eventType.split("_");
        if (parts.length > 0) {
            return parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase();
        }
        return "Unknown";
    }

    private String extractEntityId(Object payload) throws JsonProcessingException {
        // Extract entity ID from payload
        if (payload instanceof Map) {
            Map<String, Object> payloadMap = (Map<String, Object>) payload;
            if (payloadMap.containsKey("id")) {
                return String.valueOf(payloadMap.get("id"));
            }
        }
        // If we can't find an ID field, serialize the whole payload and use its hash
        return String.valueOf(payload.hashCode());
    }

    private String extractActionType(String eventType) {
        if (eventType.contains("CREATED")) {
            return ActionType.CREATE.name();
        } else if (eventType.contains("UPDATED")) {
            return ActionType.UPDATE.name();
        } else if (eventType.contains("DELETED")) {
            return ActionType.DELETE.name();
        }
        return ActionType.CREATE.name(); // Default
    }

    private String extractUserId(Map<String, Object> eventMap) {
        if (eventMap.containsKey("userId")) {
            return String.valueOf(eventMap.get("userId"));
        }
        return null;
    }

    private String extractUserInfo(Map<String, Object> eventMap) throws JsonProcessingException {
        if (eventMap.containsKey("userInfo")) {
            return objectMapper.writeValueAsString(eventMap.get("userInfo"));
        }
        return null;
    }

    private String extractOldValue(Object payload, String actionType) throws JsonProcessingException {
        if (ActionType.UPDATE.name().equals(actionType) || ActionType.DELETE.name().equals(actionType)) {
            if (payload instanceof Map) {
                Map<String, Object> payloadMap = (Map<String, Object>) payload;
                if (payloadMap.containsKey("oldValue")) {
                    return objectMapper.writeValueAsString(payloadMap.get("oldValue"));
                }
            }
            // For UPDATE and DELETE, if there's no oldValue field, use the whole payload
            return objectMapper.writeValueAsString(payload);
        }
        return null;
    }

    private String extractNewValue(Object payload, String actionType) throws JsonProcessingException {
        if (ActionType.CREATE.name().equals(actionType) || ActionType.UPDATE.name().equals(actionType)) {
            if (payload instanceof Map) {
                Map<String, Object> payloadMap = (Map<String, Object>) payload;
                if (payloadMap.containsKey("newValue")) {
                    return objectMapper.writeValueAsString(payloadMap.get("newValue"));
                }
            }
            // For CREATE and UPDATE, if there's no newValue field, use the whole payload
            return objectMapper.writeValueAsString(payload);
        }
        return null;
    }

    private String extractChanges(Object payload, String actionType) throws JsonProcessingException {
        if (ActionType.UPDATE.name().equals(actionType)) {
            if (payload instanceof Map) {
                Map<String, Object> payloadMap = (Map<String, Object>) payload;
                if (payloadMap.containsKey("changes")) {
                    return objectMapper.writeValueAsString(payloadMap.get("changes"));
                }
            }
        }
        return null;
    }

    private String extractRequestId(Map<String, Object> eventMap) {
        if (eventMap.containsKey("requestId")) {
            return String.valueOf(eventMap.get("requestId"));
        }
        return null;
    }
}