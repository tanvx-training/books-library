package com.library.notification.dto.response;

import com.library.notification.repository.NotificationType;
import com.library.notification.repository.NotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

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