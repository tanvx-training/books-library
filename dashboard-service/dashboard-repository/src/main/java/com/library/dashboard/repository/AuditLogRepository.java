package com.library.dashboard.repository;

import com.library.dashboard.repository.entity.AuditLogEntity;
import com.library.dashboard.repository.enums.ActionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID>, JpaSpecificationExecutor<AuditLogEntity> {

    Page<AuditLogEntity> findByServiceNameAndEntityName(String serviceName, String entityName, Pageable pageable);

    Page<AuditLogEntity> findByEntityId(String entityId, Pageable pageable);

    Page<AuditLogEntity> findByActionType(ActionType actionType, Pageable pageable);

    Page<AuditLogEntity> findByUserId(String userId, Pageable pageable);

    Page<AuditLogEntity> findByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    Page<AuditLogEntity> findByServiceName(String serviceName, Pageable pageable);

    Page<AuditLogEntity> findByEntityName(String entityName, Pageable pageable);

    long countByServiceName(String serviceName);

    long countByActionType(ActionType actionType);

    @Query("SELECT a FROM AuditLogEntity a WHERE a.entityId = :entityId ORDER BY a.timestamp DESC LIMIT :limit")
    List<AuditLogEntity> findRecentByEntityId(@Param("entityId") String entityId, @Param("limit") int limit);

    Page<AuditLogEntity> findByTimestampAfter(LocalDateTime date, Pageable pageable);

    Page<AuditLogEntity> findByTimestampBefore(LocalDateTime date, Pageable pageable);
}