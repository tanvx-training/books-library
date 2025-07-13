package com.library.history.infrastructure.persistence.repository;

import com.library.history.infrastructure.persistence.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, UUID> {
    
    List<AuditLogJpaEntity> findByEntityNameAndEntityId(String entityName, String entityId);
    
    List<AuditLogJpaEntity> findByUserId(String userId);
    
    List<AuditLogJpaEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
} 