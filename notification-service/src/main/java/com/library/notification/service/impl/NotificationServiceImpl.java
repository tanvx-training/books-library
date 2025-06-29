package com.library.notification.service.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.notification.service.NotificationService;
import com.library.notification.repository.NotificationRepository;
import com.library.notification.repository.NotificationTemplateRepository;
import com.library.notification.dto.UserCreatedMessage;
import com.library.notification.model.Notification;
import com.library.notification.model.NotificationTemplate;
import com.library.notification.utils.EmailUtils;
import com.library.common.enums.NotificationStatus;
import com.library.common.enums.NotificationType;
import com.library.common.event.UserCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTemplateRepository templateRepository;
    private final EmailUtils emailUtils;

    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = false, // Void method
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 8000L, // Email processing + DB operations
        messagePrefix = "NOTIFICATION_SERVICE_USER_CREATED",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "event_processing=true",
            "email_notification=true",
            "user_onboarding=true",
            "template_processing=true",
            "multi_step_operation=true"
        }
    )
    public void handleUserCreated(UserCreatedMessage message) {
        try {
            UserCreatedEvent userEvent = message.getPayload();
            
            // Find notification template for user creation
            NotificationTemplate template = templateRepository.findByName("USER_CREATED")
                    .stream()
                    .findFirst()
                    .orElse(createDefaultUserCreatedTemplate());

            // Create notification record
            Notification notification = createNotificationRecord(userEvent, template);
            notification = notificationRepository.save(notification);

            // Send email notification
            emailUtils.sendUserCreatedMail(userEvent, template);

            // Update notification status to SENT
            notification.setStatus(NotificationStatus.SENT.name());
            notification.setSentAt(java.time.LocalDateTime.now());
            notificationRepository.save(notification);

            log.info("Successfully processed user created notification for user ID: {}", userEvent.getUserId());

        } catch (Exception e) {
            UserCreatedEvent userEvent = message.getPayload();
            log.error("Failed to process user created notification for user ID: {}", userEvent.getUserId(), e);
            
            // Create failed notification record
            Notification failedNotification = createFailedNotificationRecord(userEvent, e.getMessage());
            notificationRepository.save(failedNotification);
            
            throw new RuntimeException("Notification processing failed", e);
        }
    }

    private NotificationTemplate createDefaultUserCreatedTemplate() {
        NotificationTemplate template = new NotificationTemplate();
        template.setName("USER_CREATED");
        template.setSubject("Welcome to Library Management System");
        template.setContent("Welcome {{fullname}}! Your account has been created successfully.");
        template.setType(NotificationType.EMAIL.name());
        return templateRepository.save(template);
    }

    private Notification createNotificationRecord(UserCreatedEvent userEvent, NotificationTemplate template) {
        Notification notification = new Notification();
        notification.setUserId(userEvent.getUserId());
        notification.setTemplate(template);
        notification.setTitle(template.getSubject());
        notification.setContent(processTemplate(template.getContent(), userEvent));
        notification.setType(NotificationType.EMAIL.name());
        notification.setStatus("PENDING");
        return notification;
    }

    private Notification createFailedNotificationRecord(UserCreatedEvent userEvent, String errorMessage) {
        Notification notification = new Notification();
        notification.setUserId(userEvent.getUserId());
        notification.setTitle("User Created Notification Failed");
        notification.setContent("Failed to send notification: " + errorMessage);
        notification.setType(NotificationType.EMAIL.name());
        notification.setStatus("FAILED");
        return notification;
    }

    private String processTemplate(String template, UserCreatedEvent userEvent) {
        return template
                .replace("{{fullname}}", userEvent.getFirstName() + " " + userEvent.getLastName())
                .replace("{{username}}", userEvent.getUsername())
                .replace("{{email}}", userEvent.getEmail());
    }

    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 10000L,
        messagePrefix = "NOTIFICATION_SERVICE_RETRY_FAILED",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "batch_operation=true",
            "retry_processing=true",
            "error_recovery=true"
        }
    )
    public int retryFailedNotifications(LocalDateTime fromDateTime) {
        List<Notification> failedNotifications = notificationRepository.findFailedNotificationsAfter(fromDateTime);
        int processedCount = 0;
        
        for (Notification notification : failedNotifications) {
            try {
                notification.setStatus("PENDING");
                notificationRepository.save(notification);
                processedCount++;
            } catch (Exception e) {
                log.error("Failed to retry notification {}: {}", notification.getId(), e.getMessage());
            }
        }
        
        return processedCount;
    }

    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DELETE,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 15000L,
        messagePrefix = "NOTIFICATION_SERVICE_CLEANUP",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "cleanup_operation=true",
            "maintenance_operation=true"
        }
    )
    public int cleanUpOldNotifications(LocalDateTime olderThan) {
        // Simplified cleanup - would need proper implementation
        return 0; // Placeholder
    }

    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = false,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "NOTIFICATION_SERVICE_STATISTICS",
        customTags = {
            "layer=service", 
            "analytics_operation=true",
            "admin_operation=true"
        }
    )
    public List<NotificationStatistics> getNotificationStatistics() {
        List<String> statuses = Arrays.asList("PENDING", "SENT", "FAILED");
        
        return statuses.stream()
                .map(status -> {
                    Long count = notificationRepository.countNotificationsByStatus(status);
                    return new NotificationStatistics(status, count);
                })
                .collect(Collectors.toList());
    }

    /**
     * Scheduled task to retry failed notifications
     * Runs every hour
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "Notification",
        logArguments = false,
        logReturnValue = false,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 15000L,
        messagePrefix = "NOTIFICATION_SCHEDULED_RETRY",
        customTags = {
            "layer=service", 
            "scheduled_job=true", 
            "retry_processing=true",
            "error_recovery=true",
            "maintenance_operation=true"
        }
    )
    public void scheduledRetryFailedNotifications() {
        // Retry notifications that failed in the last 24 hours
        LocalDateTime fromDateTime = LocalDateTime.now().minusHours(24);
        int processedCount = retryFailedNotifications(fromDateTime);
        log.info("Scheduled retry processed {} failed notifications", processedCount);
    }

    /**
     * Scheduled task to cleanup old notifications
     * Runs daily at 2 AM
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DELETE,
        resourceType = "Notification",
        logArguments = false,
        logReturnValue = false,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 20000L,
        messagePrefix = "NOTIFICATION_SCHEDULED_CLEANUP",
        customTags = {
            "layer=service", 
            "scheduled_job=true", 
            "cleanup_operation=true",
            "data_retention=true",
            "maintenance_operation=true"
        }
    )
    public void scheduledCleanupOldNotifications() {
        // Clean up notifications older than 30 days
        LocalDateTime olderThan = LocalDateTime.now().minusDays(30);
        int cleanedCount = cleanUpOldNotifications(olderThan);
        log.info("Scheduled cleanup removed {} old notifications", cleanedCount);
    }
}
