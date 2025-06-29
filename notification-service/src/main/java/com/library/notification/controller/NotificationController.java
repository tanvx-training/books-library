package com.library.notification.controller;

import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.notification.model.Notification;
import com.library.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationRepository notificationRepository;

    /**
     * Get notifications for a user with pagination
     */
    @GetMapping("/user/{userId}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = false, // Don't log notification collections
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "NOTIFICATION_CONTROLLER_BY_USER",
        customTags = {
            "endpoint=getNotificationsByUser", 
            "layer=controller", 
            "pagination=true",
            "user_notifications=true"
        }
    )
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        
        List<Notification> notifications = notificationRepository.findNotificationsByUserId(userId, limit, offset);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Get notification count for a user (unread)
     */
    @GetMapping("/user/{userId}/count/unread")
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "NOTIFICATION_CONTROLLER_UNREAD_COUNT",
        customTags = {
            "endpoint=getUnreadCount", 
            "layer=controller", 
            "count_operation=true",
            "user_dashboard=true"
        }
    )
    public ResponseEntity<ApiResponse<Long>> getUnreadNotificationCount(@PathVariable Long userId) {
        
        Long count = notificationRepository.countUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    /**
     * Mark notifications as read
     */
    @PatchMapping("/user/{userId}/mark-read")
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "NOTIFICATION_CONTROLLER_MARK_READ",
        customTags = {
            "endpoint=markNotificationsAsRead", 
            "layer=controller", 
            "bulk_update=true",
            "user_interaction=true",
            "read_status_update=true"
        }
    )
    public ResponseEntity<ApiResponse<Integer>> markNotificationsAsRead(
            @PathVariable Long userId,
            @RequestBody List<Long> notificationIds) {
        
        int updatedCount = notificationRepository.markNotificationsAsRead(userId, notificationIds);
        return ResponseEntity.ok(ApiResponse.success(updatedCount));
    }

    /**
     * Get notifications by status (admin operation)
     */
    @GetMapping("/status/{status}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "NOTIFICATION_CONTROLLER_BY_STATUS",
        customTags = {
            "endpoint=getNotificationsByStatus", 
            "layer=controller", 
            "admin_operation=true",
            "status_filter=true"
        }
    )
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationsByStatus(@PathVariable String status) {
        
        List<Notification> notifications = notificationRepository.findNotificationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * Get notification statistics by status (admin analytics)
     */
    @GetMapping("/analytics/status/{status}/count")
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 800L,
        messagePrefix = "NOTIFICATION_CONTROLLER_STATUS_COUNT",
        customTags = {
            "endpoint=getStatusCount", 
            "layer=controller", 
            "analytics_operation=true",
            "admin_operation=true",
            "count_operation=true"
        }
    )
    public ResponseEntity<ApiResponse<Long>> getNotificationCountByStatus(@PathVariable String status) {
        
        Long count = notificationRepository.countNotificationsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
} 