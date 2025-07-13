package com.library.history.infrastructure.persistence.impl;

import com.library.history.domain.model.audit_log.AuditLog;
import com.library.history.domain.model.audit_log.AuditLogId;
import com.library.history.domain.model.audit_log.EntityId;
import com.library.history.domain.model.audit_log.EntityName;
import com.library.history.domain.model.audit_log.UserId;
import com.library.history.domain.repository.AuditLogRepository;
import com.library.history.infrastructure.exception.AuditLogPersistenceException;
import com.library.history.infrastructure.persistence.entity.AuditLogJpaEntity;
import com.library.history.infrastructure.persistence.mapper.AuditLogJpaMapper;
import com.library.history.infrastructure.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogJpaRepository auditLogJpaRepository;
    private final AuditLogJpaMapper auditLogJpaMapper;

    @Override
    public AuditLog save(AuditLog auditLog) {
        try {
            AuditLogJpaEntity auditLogJpaEntity = auditLogJpaMapper.toJpaEntity(auditLog);
            AuditLogJpaEntity savedEntity = auditLogJpaRepository.save(auditLogJpaEntity);
            return auditLogJpaMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving audit log", e);
            throw new AuditLogPersistenceException("Failed to save audit log", e);
        }
    }

    @Override
    public Optional<AuditLog> findById(AuditLogId id) {
        try {
            return auditLogJpaRepository.findById(id.value())
                    .map(auditLogJpaMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding audit log by ID: {}", id.value(), e);
            throw new AuditLogPersistenceException("Failed to find audit log by ID: " + id.value(), e);
        }
    }

    @Override
    public List<AuditLog> findByEntityNameAndEntityId(EntityName entityName, EntityId entityId) {
        try {
            return auditLogJpaMapper.toDomainEntities(
                    auditLogJpaRepository.findByEntityNameAndEntityId(entityName.value(), entityId.value())
            );
        } catch (DataAccessException e) {
            log.error("Error finding audit logs by entity name: {} and entity ID: {}", 
                    entityName.value(), entityId.value(), e);
            throw new AuditLogPersistenceException(
                    "Failed to find audit logs by entity name: " + entityName.value() +
                    " and entity ID: " + entityId.value(), e);
        }
    }

    @Override
    public List<AuditLog> findByUserId(UserId userId) {
        try {
            return auditLogJpaMapper.toDomainEntities(
                    auditLogJpaRepository.findByUserId(userId.value())
            );
        } catch (DataAccessException e) {
            log.error("Error finding audit logs by user ID: {}", userId.value(), e);
            throw new AuditLogPersistenceException("Failed to find audit logs by user ID: " + userId.value(), e);
        }
    }

    @Override
    public List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        try {
            return auditLogJpaMapper.toDomainEntities(
                    auditLogJpaRepository.findByTimestampBetween(start, end)
            );
        } catch (DataAccessException e) {
            log.error("Error finding audit logs between {} and {}", start, end, e);
            throw new AuditLogPersistenceException("Failed to find audit logs between timestamps", e);
        }
    }

    @Override
    public void deleteById(AuditLogId id) {
        try {
            auditLogJpaRepository.deleteById(id.value());
        } catch (DataAccessException e) {
            log.error("Error deleting audit log by ID: {}", id.value(), e);
            throw new AuditLogPersistenceException("Failed to delete audit log by ID: " + id.value(), e);
        }
    }

    @Override
    public List<AuditLog> findAll() {
        try {
            return auditLogJpaMapper.toDomainEntities(auditLogJpaRepository.findAll());
        } catch (DataAccessException e) {
            log.error("Error finding all audit logs", e);
            throw new AuditLogPersistenceException("Failed to find all audit logs", e);
        }
    }
} 