package com.library.notification.domain.service;

import com.library.notification.presentation.dto.UserCreatedMessage;

public interface NotificationService {
    void handleUserCreated(UserCreatedMessage message);
}
