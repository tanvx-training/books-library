package com.library.history.application.mapper;

import com.library.history.application.dto.request.AuditLogCreateRequest;
import com.library.history.application.dto.response.AuditLogResponse;
import com.library.history.domain.model.audit_log.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {
    
    AuditLogMapper INSTANCE = Mappers.getMapper(AuditLogMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", ignore = true)
    AuditLog toAuditLog(AuditLogCreateRequest request);
    
    @Mapping(target = "id", source = "id.value")
    @Mapping(target = "serviceName", source = "serviceName.value")
    @Mapping(target = "entityName", source = "entityName.value")
    @Mapping(target = "entityId", source = "entityId.value")
    @Mapping(target = "actionType", source = "actionType")
    @Mapping(target = "userId", source = "userId.value")
    @Mapping(target = "userInfo", source = "userInfo.value")
    @Mapping(target = "oldValue", source = "oldValue.value")
    @Mapping(target = "newValue", source = "newValue.value")
    @Mapping(target = "changes", source = "changes.value")
    @Mapping(target = "requestId", source = "requestId.value")
    AuditLogResponse toAuditLogResponse(AuditLog auditLog);
    
    List<AuditLogResponse> toAuditLogResponseList(List<AuditLog> auditLogs);
}