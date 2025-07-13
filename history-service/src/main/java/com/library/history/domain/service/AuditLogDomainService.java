package com.library.history.domain.service;

import com.library.history.domain.model.audit_log.AuditLog;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditLogDomainService {

    public AuditLog createAuditLog(
            String serviceName,
            String entityName,
            String entityId,
            String actionType,
            String userId,
            String userInfo,
            String oldValue,
            String newValue,
            String changes,
            String requestId
    ) {
        return AuditLog.builder()
                .id(UUID.randomUUID())
                .serviceName(serviceName)
                .entityName(entityName)
                .entityId(entityId)
                .actionType(actionType)
                .userId(userId)
                .userInfo(userInfo)
                .oldValue(oldValue)
                .newValue(newValue)
                .changes(changes)
                .timestamp(LocalDateTime.now())
                .requestId(requestId)
                .build();
    }

    public boolean validateAuditLog(AuditLog auditLog) {
        if (auditLog == null) {
            return false;
        }
        
        // Basic validation
        if (auditLog.getServiceName() == null || auditLog.getEntityName() == null || 
            auditLog.getEntityId() == null || auditLog.getActionType() == null) {
            return false;
        }
        
        // Validate based on action type
        return switch (auditLog.getActionType()) {
            case CREATE -> auditLog.getNewValue() != null;
            case UPDATE -> auditLog.getOldValue() != null && auditLog.getNewValue() != null;
            case DELETE -> auditLog.getOldValue() != null;
        };
    }
} 