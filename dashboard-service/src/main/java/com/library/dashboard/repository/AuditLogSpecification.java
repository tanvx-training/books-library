package com.library.dashboard.repository;

import com.library.dashboard.repository.AuditLogEntity;
import com.library.dashboard.repository.ActionType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditLogSpecification {

    public static Specification<AuditLogEntity> hasServiceName(String serviceName) {
        return (root, query, criteriaBuilder) ->
                serviceName == null ? null : criteriaBuilder.equal(root.get("serviceName"), serviceName);
    }

    public static Specification<AuditLogEntity> hasEntityName(String entityName) {
        return (root, query, criteriaBuilder) ->
                entityName == null ? null : criteriaBuilder.equal(root.get("entityName"), entityName);
    }

    public static Specification<AuditLogEntity> hasEntityId(String entityId) {
        return (root, query, criteriaBuilder) ->
                entityId == null ? null : criteriaBuilder.equal(root.get("entityId"), entityId);
    }

    public static Specification<AuditLogEntity> hasActionType(ActionType actionType) {
        return (root, query, criteriaBuilder) ->
                actionType == null ? null : criteriaBuilder.equal(root.get("actionType"), actionType);
    }

    public static Specification<AuditLogEntity> hasUserId(String userId) {
        return (root, query, criteriaBuilder) ->
                userId == null ? null : criteriaBuilder.equal(root.get("userId"), userId);
    }

    public static Specification<AuditLogEntity> createdBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return (root, query, criteriaBuilder) -> {
            if (startDate == null && endDate == null) {
                return null;
            }
            if (startDate == null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate);
            }
            if (endDate == null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate);
            }
            return criteriaBuilder.between(root.get("createdAt"), startDate, endDate);
        };
    }

    public static Specification<AuditLogEntity> createdAfter(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.greaterThan(root.get("createdAt"), date);
    }

    public static Specification<AuditLogEntity> createdBefore(LocalDateTime date) {
        return (root, query, criteriaBuilder) ->
                date == null ? null : criteriaBuilder.lessThan(root.get("createdAt"), date);
    }

    public static Specification<AuditLogEntity> serviceNameContains(String serviceName) {
        return (root, query, criteriaBuilder) ->
                serviceName == null ? null : 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("serviceName")), 
                    "%" + serviceName.toLowerCase() + "%");
    }

    public static Specification<AuditLogEntity> entityNameContains(String entityName) {
        return (root, query, criteriaBuilder) ->
                entityName == null ? null : 
                criteriaBuilder.like(criteriaBuilder.lower(root.get("entityName")), 
                    "%" + entityName.toLowerCase() + "%");
    }

    public static Specification<AuditLogEntity> hasUserInfo() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("userId"));
    }

    public static Specification<AuditLogEntity> hasChanges() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isNotNull(root.get("changes"));
    }

    public static Specification<AuditLogEntity> serviceNameIn(String... serviceNames) {
        return (root, query, criteriaBuilder) ->
                serviceNames == null || serviceNames.length == 0 ? null :
                root.get("serviceName").in((Object[]) serviceNames);
    }

    public static Specification<AuditLogEntity> actionTypeIn(ActionType... actionTypes) {
        return (root, query, criteriaBuilder) ->
                actionTypes == null || actionTypes.length == 0 ? null :
                root.get("actionType").in((Object[]) actionTypes);
    }
}