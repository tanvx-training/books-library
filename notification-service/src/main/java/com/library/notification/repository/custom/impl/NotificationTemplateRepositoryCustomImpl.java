package com.library.notification.repository.custom.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.notification.model.NotificationTemplate;
import com.library.notification.repository.custom.NotificationTemplateRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class NotificationTemplateRepositoryCustomImpl implements NotificationTemplateRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "NotificationTemplate",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "TEMPLATE_REPO_ACTIVE_BY_NAME_TYPE",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "template_lookup=true",
            "active_template=true"
        }
    )
    public Optional<NotificationTemplate> findActiveTemplateByNameAndType(String name, String type) {
        
        String jpql = "SELECT nt FROM NotificationTemplate nt " +
                     "WHERE nt.name = :name AND nt.type = :type";
        
        TypedQuery<NotificationTemplate> query = entityManager.createQuery(jpql, NotificationTemplate.class);
        query.setParameter("name", name);
        query.setParameter("type", type);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "NotificationTemplate",
        logArguments = true,
        logReturnValue = false, // Don't log template collections
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "TEMPLATE_REPO_BY_TYPE",
        customTags = {
            "layer=repository", 
            "type_filter=true", 
            "admin_operation=true"
        }
    )
    public List<NotificationTemplate> findTemplatesByType(String type) {
        
        String jpql = "SELECT nt FROM NotificationTemplate nt " +
                     "WHERE nt.type = :type " +
                     "ORDER BY nt.name ASC";
        
        TypedQuery<NotificationTemplate> query = entityManager.createQuery(jpql, NotificationTemplate.class);
        query.setParameter("type", type);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "NotificationTemplate",
        logArguments = false,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "TEMPLATE_REPO_INCOMPLETE",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "maintenance_operation=true",
            "data_quality=true",
            "admin_operation=true"
        }
    )
    public List<NotificationTemplate> findIncompleteTemplates() {
        
        String jpql = "SELECT nt FROM NotificationTemplate nt " +
                     "WHERE nt.subject IS NULL OR nt.subject = '' " +
                     "OR nt.content IS NULL OR nt.content = '' " +
                     "ORDER BY nt.createdAt DESC";
        
        TypedQuery<NotificationTemplate> query = entityManager.createQuery(jpql, NotificationTemplate.class);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "NotificationTemplate",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 1200L,
        messagePrefix = "TEMPLATE_REPO_RECENTLY_USED",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "usage_analytics=true",
            "admin_dashboard=true",
            "join_query=true"
        }
    )
    public List<NotificationTemplate> findRecentlyUsedTemplates(int limit) {
        
        String jpql = "SELECT DISTINCT nt FROM NotificationTemplate nt " +
                     "JOIN Notification n ON n.template.id = nt.id " +
                     "ORDER BY n.createdAt DESC";
        
        TypedQuery<NotificationTemplate> query = entityManager.createQuery(jpql, NotificationTemplate.class);
        query.setMaxResults(limit);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "NotificationTemplate",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "TEMPLATE_REPO_USAGE_COUNT",
        customTags = {
            "layer=repository", 
            "analytics_query=true", 
            "count_operation=true",
            "usage_statistics=true"
        }
    )
    public Long getTemplateUsageCount(Long templateId) {
        
        String jpql = "SELECT COUNT(n) FROM Notification n WHERE n.template.id = :templateId";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("templateId", templateId);
        
        return query.getSingleResult();
    }
} 