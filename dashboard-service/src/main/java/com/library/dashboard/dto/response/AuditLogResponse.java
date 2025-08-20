package com.library.dashboard.dto.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.library.dashboard.repository.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {

    private UUID id;
    private String serviceName;
    private String entityName;
    private String entityId;
    private ActionType actionType;
    private String userId;
    private JsonNode userInfo;
    private JsonNode oldValue;
    private JsonNode newValue;
    private JsonNode changes;
    private LocalDateTime createdAt;
}