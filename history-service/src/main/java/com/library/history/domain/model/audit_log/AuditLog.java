package com.library.history.domain.model.audit_log;

import com.library.history.domain.model.shared.AggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class AuditLog extends AggregateRoot {
    private final AuditLogId id;
    private final ServiceName serviceName;
    private final EntityName entityName;
    private final EntityId entityId;
    private final ActionType actionType;
    private final UserId userId;
    private final UserInfo userInfo;
    private final JsonData oldValue;
    private final JsonData newValue;
    private final JsonData changes;
    private final LocalDateTime timestamp;
    private final RequestId requestId;

    private AuditLog(Builder builder) {
        this.id = builder.id;
        this.serviceName = builder.serviceName;
        this.entityName = builder.entityName;
        this.entityId = builder.entityId;
        this.actionType = builder.actionType;
        this.userId = builder.userId;
        this.userInfo = builder.userInfo;
        this.oldValue = builder.oldValue;
        this.newValue = builder.newValue;
        this.changes = builder.changes;
        this.timestamp = builder.timestamp;
        this.requestId = builder.requestId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private AuditLogId id;
        private ServiceName serviceName;
        private EntityName entityName;
        private EntityId entityId;
        private ActionType actionType;
        private UserId userId;
        private UserInfo userInfo;
        private JsonData oldValue;
        private JsonData newValue;
        private JsonData changes;
        private LocalDateTime timestamp;
        private RequestId requestId;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = new AuditLogId(id);
            return this;
        }

        public Builder serviceName(String serviceName) {
            this.serviceName = new ServiceName(serviceName);
            return this;
        }

        public Builder entityName(String entityName) {
            this.entityName = new EntityName(entityName);
            return this;
        }

        public Builder entityId(String entityId) {
            this.entityId = new EntityId(entityId);
            return this;
        }

        public Builder actionType(String actionType) {
            this.actionType = ActionType.valueOf(actionType);
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId != null ? new UserId(userId) : null;
            return this;
        }

        public Builder userInfo(String userInfo) {
            this.userInfo = userInfo != null ? new UserInfo(userInfo) : null;
            return this;
        }

        public Builder oldValue(String oldValue) {
            this.oldValue = oldValue != null ? new JsonData(oldValue) : null;
            return this;
        }

        public Builder newValue(String newValue) {
            this.newValue = newValue != null ? new JsonData(newValue) : null;
            return this;
        }

        public Builder changes(String changes) {
            this.changes = changes != null ? new JsonData(changes) : null;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder requestId(String requestId) {
            this.requestId = requestId != null ? new RequestId(requestId) : null;
            return this;
        }

        public AuditLog build() {
            return new AuditLog(this);
        }
    }
} 