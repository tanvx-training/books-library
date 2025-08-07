package com.library.notification.business.dto.request;

import com.library.notification.repository.enums.NotificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request DTO for updating notification status
 */
@Data
public class UpdateNotificationStatusRequest {

    @NotNull(message = "Notification status is required")
    private NotificationStatus status;
}