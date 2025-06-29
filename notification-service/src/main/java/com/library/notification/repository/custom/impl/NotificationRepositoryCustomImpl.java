package com.library.notification.repository.custom.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.notification.model.Notification;
import com.library.notification.repository.custom.NotificationRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = false, // Don't log notification collections
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "NOTIFICATION_REPO_BY_USER_PAGINATED",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "pagination=true",
            "user_notifications=true"
        }
    )
    public List<Notification> findNotificationsByUserId(Long userId, int limit, int offset) {
        
        String jpql = "SELECT n FROM Notification n " +
                     "LEFT JOIN FETCH n.template t " +
                     "WHERE n.userId = :userId " +
                     "ORDER BY n.createdAt DESC";
        
        TypedQuery<Notification> query = entityManager.createQuery(jpql, Notification.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);
        query.setFirstResult(offset);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "NOTIFICATION_REPO_BY_STATUS",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "status_filter=true",
            "admin_operation=true"
        }
    )
    public List<Notification> findNotificationsByStatus(String status) {
        
        String jpql = "SELECT n FROM Notification n " +
                     "LEFT JOIN FETCH n.template t " +
                     "WHERE n.status = :status " +
                     "ORDER BY n.createdAt DESC";
        
        TypedQuery<Notification> query = entityManager.createQuery(jpql, Notification.class);
        query.setParameter("status", status);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "NOTIFICATION_REPO_FAILED_AFTER",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "failed_notifications=true",
            "retry_processing=true",
            "error_recovery=true"
        }
    )
    public List<Notification> findFailedNotificationsAfter(LocalDateTime fromDateTime) {
        
        String jpql = "SELECT n FROM Notification n " +
                     "LEFT JOIN FETCH n.template t " +
                     "WHERE n.status = 'FAILED' " +
                     "AND n.createdAt >= :fromDateTime " +
                     "ORDER BY n.createdAt ASC";
        
        TypedQuery<Notification> query = entityManager.createQuery(jpql, Notification.class);
        query.setParameter("fromDateTime", fromDateTime);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "NOTIFICATION_REPO_COUNT_STATUS",
        customTags = {
            "layer=repository", 
            "analytics_query=true", 
            "count_operation=true",
            "admin_operation=true"
        }
    )
    public Long countNotificationsByStatus(String status) {
        
        String jpql = "SELECT COUNT(n) FROM Notification n WHERE n.status = :status";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("status", status);
        
        return query.getSingleResult();
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "NOTIFICATION_REPO_COUNT_UNREAD",
        customTags = {
            "layer=repository", 
            "count_operation=true", 
            "unread_notifications=true",
            "user_dashboard=true"
        }
    )
    public Long countUnreadNotificationsByUserId(Long userId) {
        
        String jpql = "SELECT COUNT(n) FROM Notification n " +
                     "WHERE n.userId = :userId AND n.readAt IS NULL";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("userId", userId);
        
        return query.getSingleResult();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "NOTIFICATION_REPO_MARK_READ",
        customTags = {
            "layer=repository", 
            "bulk_update=true", 
            "read_status_update=true",
            "user_interaction=true",
            "batch_operation=true"
        }
    )
    public int markNotificationsAsRead(Long userId, List<Long> notificationIds) {
        
        if (notificationIds == null || notificationIds.isEmpty()) {
            return 0;
        }
        
        String jpql = "UPDATE Notification n " +
                     "SET n.readAt = :readAt " +
                     "WHERE n.userId = :userId " +
                     "AND n.id IN :notificationIds " +
                     "AND n.readAt IS NULL";
        
        return entityManager.createQuery(jpql)
                .setParameter("readAt", LocalDateTime.now())
                .setParameter("userId", userId)
                .setParameter("notificationIds", notificationIds)
                .executeUpdate();
    }
} 