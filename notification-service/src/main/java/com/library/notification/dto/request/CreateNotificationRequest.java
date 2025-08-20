package com.library.notification.dto.request;

import com.library.notification.repository.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

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