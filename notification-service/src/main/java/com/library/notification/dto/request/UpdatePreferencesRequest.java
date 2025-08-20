package com.library.notification.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePreferencesRequest {

    @NotNull(message = "Email enabled preference is required")
    private Boolean emailEnabled;

    @NotNull(message = "SMS enabled preference is required")
    private Boolean smsEnabled;

    @NotNull(message = "Push enabled preference is required")
    private Boolean pushEnabled;

    @NotNull(message = "Borrow notification preference is required")
    private Boolean borrowNotification;

    @NotNull(message = "Return reminder preference is required")
    private Boolean returnReminder;

    @NotNull(message = "Overdue notification preference is required")
    private Boolean overdueNotification;

    @NotNull(message = "Reservation notification preference is required")
    private Boolean reservationNotification;
}