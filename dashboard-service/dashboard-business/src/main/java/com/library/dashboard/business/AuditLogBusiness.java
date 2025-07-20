package com.library.dashboard.business;

import com.library.dashboard.business.dto.event.AuditEventMessage;
import com.library.dashboard.business.dto.request.AuditLogSearchCriteria;
import com.library.dashboard.business.dto.response.AuditLogResponse;
import com.library.dashboard.business.dto.response.PagedAuditLogResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AuditLogBusiness {

    PagedAuditLogResponse findAll(Pageable pageable);

    PagedAuditLogResponse search(AuditLogSearchCriteria criteria, Pageable pageable);

    AuditLogResponse findById(UUID id);

    @Transactional
    AuditLogResponse createAuditLog(AuditEventMessage eventMessage);
}
