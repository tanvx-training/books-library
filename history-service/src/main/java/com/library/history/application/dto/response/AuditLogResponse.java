package com.library.history.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private UUID id;
    private String serviceName;
    private String entityName;
    private String entityId;
    private String actionType;
    private String userId;
    private String userInfo;
    private String oldValue;
    private String newValue;
    private String changes;
    private LocalDateTime timestamp;
    private String requestId;
} 