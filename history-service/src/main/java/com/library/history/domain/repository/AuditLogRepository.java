package com.library.history.domain.repository;

import com.library.history.domain.model.audit_log.AuditLog;
import com.library.history.domain.model.audit_log.AuditLogId;
import com.library.history.domain.model.audit_log.EntityId;
import com.library.history.domain.model.audit_log.EntityName;
import com.library.history.domain.model.audit_log.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditLogRepository {
    AuditLog save(AuditLog auditLog);
    
    Optional<AuditLog> findById(AuditLogId id);
    
    List<AuditLog> findByEntityNameAndEntityId(EntityName entityName, EntityId entityId);
    
    List<AuditLog> findByUserId(UserId userId);
    
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    void deleteById(AuditLogId id);
    
    List<AuditLog> findAll();
}