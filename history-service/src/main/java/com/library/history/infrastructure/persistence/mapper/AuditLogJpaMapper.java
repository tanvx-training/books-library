package com.library.history.infrastructure.persistence.mapper;

import com.library.history.domain.model.audit_log.AuditLog;
import com.library.history.infrastructure.persistence.entity.AuditLogJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuditLogJpaMapper {

    public AuditLogJpaEntity toJpaEntity(AuditLog auditLog) {
        return AuditLogJpaEntity.builder()
                .id(auditLog.getId().value())
                .serviceName(auditLog.getServiceName().value())
                .entityName(auditLog.getEntityName().value())
                .entityId(auditLog.getEntityId().value())
                .actionType(auditLog.getActionType().name())
                .userId(auditLog.getUserId() != null ? auditLog.getUserId().value() : null)
                .userInfo(auditLog.getUserInfo() != null ? auditLog.getUserInfo().value() : null)
                .oldValue(auditLog.getOldValue() != null ? auditLog.getOldValue().value() : null)
                .newValue(auditLog.getNewValue() != null ? auditLog.getNewValue().value() : null)
                .changes(auditLog.getChanges() != null ? auditLog.getChanges().value() : null)
                .timestamp(auditLog.getTimestamp())
                .requestId(auditLog.getRequestId() != null ? auditLog.getRequestId().value() : null)
                .build();
    }

    public AuditLog toDomainEntity(AuditLogJpaEntity jpaEntity) {
        return AuditLog.builder()
                .id(jpaEntity.getId())
                .serviceName(jpaEntity.getServiceName())
                .entityName(jpaEntity.getEntityName())
                .entityId(jpaEntity.getEntityId())
                .actionType(jpaEntity.getActionType())
                .userId(jpaEntity.getUserId())
                .userInfo(jpaEntity.getUserInfo())
                .oldValue(jpaEntity.getOldValue())
                .newValue(jpaEntity.getNewValue())
                .changes(jpaEntity.getChanges())
                .timestamp(jpaEntity.getTimestamp())
                .requestId(jpaEntity.getRequestId())
                .build();
    }

    public List<AuditLog> toDomainEntities(List<AuditLogJpaEntity> jpaEntities) {
        return jpaEntities.stream()
                .map(this::toDomainEntity)
                .collect(Collectors.toList());
    }
} 