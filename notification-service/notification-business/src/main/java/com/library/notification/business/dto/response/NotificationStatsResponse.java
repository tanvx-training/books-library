package com.library.notification.business.dto.response;

import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for notification statistics
 */
@Data
@Builder
public class NotificationStatsResponse {

    private long totalNotifications;
    private long sentNotifications;
    private long deliveredNotifications;
    private long readNotifications;
    private long failedNotifications;
    
    private Map<String, Long> notificationsByType;
    private Map<String, Long> notificationsByStatus;
    
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    
    private double deliveryRate;
    private double readRate;
    private double failureRate;
}