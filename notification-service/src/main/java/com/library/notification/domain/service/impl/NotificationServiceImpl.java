package com.library.notification.domain.service.impl;

import com.library.notification.domain.service.NotificationService;
import com.library.notification.infrastructure.repository.NotificationRepository;
import com.library.notification.presentation.dto.UserCreatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void handleUserCreated(UserCreatedMessage message) {

    }
}
