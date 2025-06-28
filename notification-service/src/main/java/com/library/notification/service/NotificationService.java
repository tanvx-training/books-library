package com.library.notification.service;

import com.library.notification.dto.UserCreatedMessage;

public interface NotificationService {
    void handleUserCreated(UserCreatedMessage message);
}
