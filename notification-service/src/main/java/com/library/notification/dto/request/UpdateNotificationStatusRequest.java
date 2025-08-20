package com.library.notification.dto.request;

import com.library.notification.repository.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateNotificationStatusRequest {

    @NotNull(message = "Notification status is required")
    private NotificationStatus status;
}