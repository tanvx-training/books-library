package com.library.notification.repository.custom;

import com.library.notification.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Custom repository interface for Notification with advanced operations that need logging
 */
public interface NotificationRepositoryCustom {
    
    /**
     * Find notifications by user ID with pagination support
     * @param userId the user ID
     * @param limit max number of results
     * @param offset starting position
     * @return list of notifications
     */
    List<Notification> findNotificationsByUserId(Long userId, int limit, int offset);
    
    /**
     * Find notifications by status for admin operations
     * @param status the notification status
     * @return list of notifications
     */
    List<Notification> findNotificationsByStatus(String status);
    
    /**
     * Find failed notifications for retry processing
     * @param fromDateTime from this time
     * @return list of failed notifications
     */
    List<Notification> findFailedNotificationsAfter(LocalDateTime fromDateTime);
    
    /**
     * Count notifications by status for analytics
     * @param status the status to count
     * @return count of notifications
     */
    Long countNotificationsByStatus(String status);
    
    /**
     * Count unread notifications for a user
     * @param userId the user ID
     * @return count of unread notifications
     */
    Long countUnreadNotificationsByUserId(Long userId);
    
    /**
     * Mark notifications as read for a user
     * @param userId the user ID
     * @param notificationIds list of notification IDs to mark as read
     * @return number of updated notifications
     */
    int markNotificationsAsRead(Long userId, List<Long> notificationIds);
} 