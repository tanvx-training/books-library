package com.library.notification.business.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for notification preferences data
 */
@Data
public class NotificationPreferencesResponse {

    private UUID userPublicId;
    private Boolean emailEnabled;
    private Boolean smsEnabled;
    private Boolean pushEnabled;
    private Boolean borrowNotification;
    private Boolean returnReminder;
    private Boolean overdueNotification;
    private Boolean reservationNotification;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}