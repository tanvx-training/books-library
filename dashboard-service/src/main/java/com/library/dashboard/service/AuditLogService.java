package com.library.dashboard.service;

import com.library.dashboard.framework.kafka.AuditEventMessage;
import com.library.dashboard.dto.request.AuditLogSearchCriteria;
import com.library.dashboard.dto.response.AuditLogResponse;
import com.library.dashboard.dto.response.PagedAuditLogResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AuditLogService {

    PagedAuditLogResponse findAll(Pageable pageable);

    PagedAuditLogResponse search(AuditLogSearchCriteria criteria, Pageable pageable);

    AuditLogResponse findById(UUID id);

    @Transactional
    AuditLogResponse createAuditLog(AuditEventMessage eventMessage);
}
