package com.library.dashboard.service;

import com.library.dashboard.framework.kafka.AuditEventMessage;
import com.library.dashboard.dto.request.AuditLogSearchCriteria;
import com.library.dashboard.dto.response.AuditLogResponse;
import com.library.dashboard.dto.response.PagedAuditLogResponse;
import com.library.dashboard.aop.AuditLogNotFoundException;
import com.library.dashboard.repository.AuditLogRepository;
import com.library.dashboard.repository.AuditLogEntity;
import com.library.dashboard.repository.AuditLogSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;

    @Override
    public PagedAuditLogResponse findAll(Pageable pageable) {

        Page<AuditLogEntity> entities = auditLogRepository.findAll(pageable);
        return auditLogMapper.toPagedResponse(entities);
    }

    @Override
    public PagedAuditLogResponse search(AuditLogSearchCriteria criteria, Pageable pageable) {

        Specification<AuditLogEntity> spec = buildSpecification(criteria);
        Page<AuditLogEntity> entities = auditLogRepository.findAll(spec, pageable);
        return auditLogMapper.toPagedResponse(entities);

    }

    @Override
    public AuditLogResponse findById(UUID id) {

        return auditLogRepository.findById(id)
                .map(auditLogMapper::toDto)
                .orElseThrow(() -> new AuditLogNotFoundException(id));
    }

    @Transactional
    @Override
    public AuditLogResponse createAuditLog(AuditEventMessage eventMessage) {

        AuditLogEntity entity = auditLogMapper.toEntity(eventMessage);
        auditLogRepository.save(entity);

        return auditLogMapper.toDto(entity);
    }

    private Specification<AuditLogEntity> buildSpecification(AuditLogSearchCriteria criteria) {
        log.debug("Building specification from criteria: {}", criteria);

        if (criteria == null) {
            return Specification.where(null);
        }

        Specification<AuditLogEntity> spec = Specification.where(null);

        if (criteria.getServiceName() != null) {
            spec = spec.and(AuditLogSpecification.hasServiceName(criteria.getServiceName()));
            log.debug("Added service name filter: {}", criteria.getServiceName());
        }

        if (criteria.getEntityName() != null) {
            spec = spec.and(AuditLogSpecification.hasEntityName(criteria.getEntityName()));
            log.debug("Added entity name filter: {}", criteria.getEntityName());
        }

        if (criteria.getEntityId() != null) {
            spec = spec.and(AuditLogSpecification.hasEntityId(criteria.getEntityId()));
            log.debug("Added entity ID filter: {}", criteria.getEntityId());
        }

        if (criteria.getActionType() != null) {
            spec = spec.and(AuditLogSpecification.hasActionType(criteria.getActionType()));
            log.debug("Added action type filter: {}", criteria.getActionType());
        }

        if (criteria.getUserId() != null) {
            spec = spec.and(AuditLogSpecification.hasUserId(criteria.getUserId()));
            log.debug("Added user ID filter: {}", criteria.getUserId());
        }

        if (criteria.getStartDate() != null || criteria.getEndDate() != null) {
            spec = spec.and(AuditLogSpecification.createdBetween(criteria.getStartDate(), criteria.getEndDate()));
            log.debug("Added date range filter: start={}, end={}", criteria.getStartDate(), criteria.getEndDate());
        }

        return spec;
    }
}