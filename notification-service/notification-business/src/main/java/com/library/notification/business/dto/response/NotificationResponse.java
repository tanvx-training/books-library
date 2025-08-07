package com.library.notification.business.dto.response;

import com.library.notification.repository.enums.NotificationType;
import com.library.notification.repository.enums.NotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for notification data
 */
@Data
public class NotificationResponse {

    private UUID publicId;
    private UUID userPublicId;
    private String title;
    private String content;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}