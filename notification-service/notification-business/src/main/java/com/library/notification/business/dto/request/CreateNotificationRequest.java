package com.library.notification.business.dto.request;

import com.library.notification.repository.enums.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * Request DTO for creating a new notification
 */
@Data
public class CreateNotificationRequest {

    @NotNull(message = "User public ID is required")
    private UUID userPublicId;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @NotNull(message = "Notification type is required")
    private NotificationType type;
}