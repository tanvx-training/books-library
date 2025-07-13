package com.library.history.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "audit_logs")
public class AuditLogJpaEntity {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "service_name", nullable = false, length = 100)
    private String serviceName;
    
    @Column(name = "entity_name", nullable = false, length = 100)
    private String entityName;
    
    @Column(name = "entity_id", nullable = false)
    private String entityId;
    
    @Column(name = "action_type", nullable = false, length = 20)
    private String actionType;
    
    @Column(name = "user_id", length = 36)
    private String userId;
    
    @Column(name = "user_info", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String userInfo;
    
    @Column(name = "old_value", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String oldValue;
    
    @Column(name = "new_value", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String newValue;
    
    @Column(name = "changes", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String changes;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @Column(name = "request_id", length = 100)
    private String requestId;
} 