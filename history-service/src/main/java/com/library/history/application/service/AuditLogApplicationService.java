package com.library.history.application.service;

import com.library.history.application.dto.request.AuditLogCreateRequest;
import com.library.history.application.dto.response.AuditLogResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogApplicationService {
    
    AuditLogResponse createAuditLog(AuditLogCreateRequest request);
    
    AuditLogResponse getAuditLogById(UUID id);
    
    List<AuditLogResponse> getAuditLogsByEntityNameAndEntityId(String entityName, String entityId);
    
    List<AuditLogResponse> getAuditLogsByUserId(String userId);
    
    List<AuditLogResponse> getAuditLogsByTimeRange(LocalDateTime start, LocalDateTime end);
    
    void deleteAuditLog(UUID id);
    
    List<AuditLogResponse> getAllAuditLogs();
} 