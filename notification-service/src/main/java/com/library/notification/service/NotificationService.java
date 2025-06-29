package com.library.notification.service;

import com.library.notification.dto.UserCreatedMessage;
import com.library.notification.model.Notification;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationService {
    
    void handleUserCreated(UserCreatedMessage message);
    
    /**
     * Retry failed notifications
     * @param fromDateTime retry notifications failed after this time
     * @return number of notifications processed
     */
    int retryFailedNotifications(LocalDateTime fromDateTime);
    
    /**
     * Clean up old processed notifications
     * @param olderThan clean notifications older than this date
     * @return number of notifications cleaned
     */
    int cleanUpOldNotifications(LocalDateTime olderThan);
    
    /**
     * Get notification statistics
     * @return list of statistics
     */
    List<NotificationStatistics> getNotificationStatistics();
    
    /**
     * Inner class for notification statistics
     */
    class NotificationStatistics {
        private String status;
        private Long count;
        
        public NotificationStatistics(String status, Long count) {
            this.status = status;
            this.count = count;
        }
        
        // Getters
        public String getStatus() { return status; }
        public Long getCount() { return count; }
    }
}
