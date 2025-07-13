package com.library.history.application.service.impl;

import com.library.history.application.dto.request.AuditLogCreateRequest;
import com.library.history.application.dto.response.AuditLogResponse;
import com.library.history.application.exception.AuditLogApplicationException;
import com.library.history.application.mapper.AuditLogMapper;
import com.library.history.application.service.AuditLogApplicationService;
import com.library.history.domain.exception.AuditLogNotFoundException;
import com.library.history.domain.model.audit_log.AuditLog;
import com.library.history.domain.model.audit_log.AuditLogId;
import com.library.history.domain.model.audit_log.EntityId;
import com.library.history.domain.model.audit_log.EntityName;
import com.library.history.domain.model.audit_log.UserId;
import com.library.history.domain.repository.AuditLogRepository;
import com.library.history.domain.service.AuditLogDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogApplicationServiceImpl implements AuditLogApplicationService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogDomainService auditLogDomainService;
    private final AuditLogMapper auditLogMapper;

    @Override
    @Transactional
    public AuditLogResponse createAuditLog(AuditLogCreateRequest request) {
        try {
            log.info("Creating audit log for entity: {}, id: {}", request.getEntityName(), request.getEntityId());
            
            AuditLog auditLog = auditLogDomainService.createAuditLog(
                    request.getServiceName(),
                    request.getEntityName(),
                    request.getEntityId(),
                    request.getActionType(),
                    request.getUserId(),
                    request.getUserInfo(),
                    request.getOldValue(),
                    request.getNewValue(),
                    request.getChanges(),
                    request.getRequestId()
            );
            
            if (!auditLogDomainService.validateAuditLog(auditLog)) {
                throw new AuditLogApplicationException("Invalid audit log data");
            }
            
            AuditLog savedAuditLog = auditLogRepository.save(auditLog);
            log.info("Successfully created audit log with ID: {}", savedAuditLog.getId().value());
            
            return auditLogMapper.toAuditLogResponse(savedAuditLog);
        } catch (Exception e) {
            log.error("Error creating audit log", e);
            throw new AuditLogApplicationException("Failed to create audit log", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogResponse getAuditLogById(UUID id) {
        log.info("Fetching audit log with ID: {}", id);
        return auditLogRepository.findById(new AuditLogId(id))
                .map(auditLogMapper::toAuditLogResponse)
                .orElseThrow(() -> new AuditLogNotFoundException("Audit log not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByEntityNameAndEntityId(String entityName, String entityId) {
        log.info("Fetching audit logs for entity: {}, id: {}", entityName, entityId);
        return auditLogMapper.toAuditLogResponseList(
                auditLogRepository.findByEntityNameAndEntityId(new EntityName(entityName), new EntityId(entityId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByUserId(String userId) {
        log.info("Fetching audit logs for user ID: {}", userId);
        return auditLogMapper.toAuditLogResponseList(
                auditLogRepository.findByUserId(new UserId(userId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAuditLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        log.info("Fetching audit logs between {} and {}", start, end);
        return auditLogMapper.toAuditLogResponseList(
                auditLogRepository.findByTimestampBetween(start, end)
        );
    }

    @Override
    @Transactional
    public void deleteAuditLog(UUID id) {
        log.info("Deleting audit log with ID: {}", id);
        AuditLogId auditLogId = new AuditLogId(id);
        if (auditLogRepository.findById(auditLogId).isEmpty()) {
            throw new AuditLogNotFoundException("Audit log not found with ID: " + id);
        }
        auditLogRepository.deleteById(auditLogId);
        log.info("Successfully deleted audit log with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllAuditLogs() {
        log.info("Fetching all audit logs");
        return auditLogMapper.toAuditLogResponseList(auditLogRepository.findAll());
    }
} 