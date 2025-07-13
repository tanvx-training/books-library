package com.library.history.domain.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.history.application.dto.request.AuditLogCreateRequest;
import com.library.history.application.service.AuditLogApplicationService;
import com.library.history.domain.model.audit_log.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditLogApplicationService auditLogApplicationService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = {
        "book-service-audit-logs", 
        "user-service-audit-logs", 
        "lending-service-audit-logs"
    })
    public void consumeAuditEvent(Object message, Acknowledgment acknowledgment) {
        try {
            log.info("Received audit event: {}", message);
            
            // Convert to JsonNode for flexible processing
            JsonNode eventNode = objectMapper.valueToTree(message);
            
            String eventType = eventNode.path("eventType").asText();
            String entityType = eventNode.path("entityType").asText();
            String entityId = eventNode.path("entityId").asText();
            String serviceName = extractServiceName(eventType);
            String actionType = extractActionType(eventType);
            
            AuditLogCreateRequest request = AuditLogCreateRequest.builder()
                    .serviceName(serviceName)
                    .entityName(entityType)
                    .entityId(entityId)
                    .actionType(actionType)
                    .userId(eventNode.path("userId").asText(null))
                    .userInfo(eventNode.path("userInfo").asText(null))
                    .oldValue(eventNode.path("oldValue").asText(null))
                    .newValue(eventNode.path("newValue").asText(null))
                    .changes(eventNode.path("changes").asText(null))
                    .requestId(eventNode.path("requestId").asText(null))
                    .build();
            
            auditLogApplicationService.createAuditLog(request);
            log.info("Successfully processed audit event for {}: {}", entityType, entityId);
            
            // Acknowledge the message after successful processing
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error processing audit event: {}", message, e);
            // Don't acknowledge - message will be redelivered
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
}