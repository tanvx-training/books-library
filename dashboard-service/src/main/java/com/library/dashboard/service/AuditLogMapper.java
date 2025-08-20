package com.library.dashboard.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dashboard.framework.kafka.AuditEventMessage;
import com.library.dashboard.dto.request.AuditLogSearchCriteria;
import com.library.dashboard.dto.request.AuditLogSearchRequest;
import com.library.dashboard.dto.response.AuditLogResponse;
import com.library.dashboard.dto.response.PagedAuditLogResponse;
import com.library.dashboard.repository.AuditLogEntity;
import com.library.dashboard.repository.ActionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditLogMapper {

    private final ObjectMapper objectMapper;

    public AuditLogResponse toDto(AuditLogEntity entity) {
        if (entity == null) {
            return null;
        }
        AuditLogResponse dto = new AuditLogResponse();
        dto.setId(entity.getId());
        dto.setServiceName(entity.getServiceName());
        dto.setEntityName(entity.getEntityName());
        dto.setEntityId(entity.getEntityId());
        dto.setActionType(entity.getActionType());
        dto.setUserId(entity.getUserId());
        dto.setCreatedAt(entity.getTimestamp());
        dto.setUserInfo(stringToJsonNode(entity.getUserInfo()));
        dto.setOldValue(stringToJsonNode(entity.getOldValue()));
        dto.setNewValue(stringToJsonNode(entity.getNewValue()));
        dto.setChanges(stringToJsonNode(entity.getChanges()));
        return dto;
    }

    public AuditLogEntity toEntity(AuditLogResponse dto) {
        if (dto == null) {
            return null;
        }
        AuditLogEntity entity = new AuditLogEntity();
        entity.setId(dto.getId());
        entity.setServiceName(dto.getServiceName());
        entity.setEntityName(dto.getEntityName());
        entity.setEntityId(dto.getEntityId());
        entity.setActionType(dto.getActionType());
        entity.setUserId(dto.getUserId());
        entity.setTimestamp(dto.getCreatedAt());
        entity.setUserInfo(jsonNodeToString(dto.getUserInfo()));
        entity.setOldValue(jsonNodeToString(dto.getOldValue()));
        entity.setNewValue(jsonNodeToString(dto.getNewValue()));
        entity.setChanges(jsonNodeToString(dto.getChanges()));
        return entity;
    }

    public AuditLogEntity toEntity(AuditEventMessage eventMessage) {
        if (eventMessage == null) {
            return null;
        }
        AuditLogEntity entity = new AuditLogEntity();
        entity.setServiceName(eventMessage.getServiceName());
        entity.setEntityName(eventMessage.getEntityType());
        entity.setEntityId(eventMessage.getEntityId());
        entity.setActionType(mapEventTypeToActionType(eventMessage.getEventType()));
        entity.setUserId(eventMessage.getUserId());
        entity.setUserInfo(eventMessage.getUserInfo());
        entity.setOldValue(eventMessage.getOldValue());
        entity.setNewValue(eventMessage.getNewValue());
        entity.setChanges(eventMessage.getChanges());
        entity.setTimestamp(eventMessage.getTimestamp());
        return entity;
    }

    public AuditLogResponse toDto(AuditEventMessage eventMessage) {
        if (eventMessage == null) {
            return null;
        }
        AuditLogResponse dto = new AuditLogResponse();
        dto.setServiceName(eventMessage.getServiceName());
        dto.setEntityName(eventMessage.getEntityType());
        dto.setEntityId(eventMessage.getEntityId());
        dto.setActionType(mapEventTypeToActionType(eventMessage.getEventType()));
        dto.setUserId(eventMessage.getUserId());
        dto.setCreatedAt(eventMessage.getTimestamp());
        dto.setUserInfo(stringToJsonNode(eventMessage.getUserInfo()));
        dto.setOldValue(stringToJsonNode(eventMessage.getOldValue()));
        dto.setNewValue(stringToJsonNode(eventMessage.getNewValue()));
        dto.setChanges(stringToJsonNode(eventMessage.getChanges()));
        return dto;
    }

    public PagedAuditLogResponse toPagedResponse(Page<AuditLogEntity> page) {
        if (page == null) {
            return null;
        }

        PagedAuditLogResponse response = new PagedAuditLogResponse();

        // Convert content
        List<AuditLogResponse> content = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        response.setContent(content);

        // Set pagination metadata
        response.setPageNumber(page.getNumber());
        response.setPageSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());

        return response;
    }

    public AuditLogSearchCriteria toCriteria(AuditLogSearchRequest request) {
        if (request == null) {
            return null;
        }
        AuditLogSearchCriteria criteria = new AuditLogSearchCriteria();
        criteria.setServiceName(request.getServiceName());
        criteria.setEntityName(request.getEntityName());
        criteria.setEntityId(request.getEntityId());
        criteria.setActionType(request.getActionType());
        criteria.setUserId(request.getUserId());
        criteria.setStartDate(request.getStartDate());
        criteria.setEndDate(request.getEndDate());
        return criteria;
    }

    private ActionType mapEventTypeToActionType(String eventType) {
        if (eventType == null) {
            return ActionType.UPDATE; // fallback
        }
        String upper = eventType.toUpperCase();
        if (upper.contains("CREATE") || upper.contains("CREATED")) {
            return ActionType.CREATE;
        } else if (upper.contains("UPDATE") || upper.contains("UPDATED")) {
            return ActionType.UPDATE;
        } else if (upper.contains("DELETE") || upper.contains("DELETED")) {
            return ActionType.DELETE;
        }
        return ActionType.UPDATE;
    }

    private JsonNode stringToJsonNode(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
             log.error("Invalid JSON String: {}", jsonString, e);
            return null;
        }
    }

    private String jsonNodeToString(JsonNode jsonNode) {
        if (jsonNode == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
             log.error("Cannot serialize JsonNode: {}", jsonNode, e);
            return null;
        }
    }
}
