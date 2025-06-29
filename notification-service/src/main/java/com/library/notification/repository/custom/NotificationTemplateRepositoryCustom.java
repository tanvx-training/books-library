package com.library.notification.repository.custom;

import com.library.notification.model.NotificationTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Custom repository interface for NotificationTemplate with advanced operations that need logging
 */
public interface NotificationTemplateRepositoryCustom {
    
    /**
     * Find active template by name and type
     * @param name the template name
     * @param type the notification type
     * @return optional template
     */
    Optional<NotificationTemplate> findActiveTemplateByNameAndType(String name, String type);
    
    /**
     * Find all templates by type for admin operations
     * @param type the notification type
     * @return list of templates
     */
    List<NotificationTemplate> findTemplatesByType(String type);
    
    /**
     * Find templates that need maintenance (missing subject or content)
     * @return list of incomplete templates
     */
    List<NotificationTemplate> findIncompleteTemplates();
    
    /**
     * Find most recently used templates for admin dashboard
     * @param limit max number of results
     * @return list of recently used templates
     */
    List<NotificationTemplate> findRecentlyUsedTemplates(int limit);
    
    /**
     * Get template usage statistics
     * @param templateId the template ID
     * @return usage count
     */
    Long getTemplateUsageCount(Long templateId);
} 