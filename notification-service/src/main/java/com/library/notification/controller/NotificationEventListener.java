package com.library.notification.controller;

import com.library.common.constants.EventType;
import com.library.notification.service.NotificationService;
import com.library.notification.dto.UserCreatedMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = {EventType.USER_CREATED},
            groupId = "book-group",
            properties = {"spring.json.value.default.type=com.library.notification.dto.UserCreatedMessage"}
    )
    @SneakyThrows
    public void notifyUserCreated(@Payload UserCreatedMessage message) {
        log.info("Received user created event: {}", message);
        notificationService.handleUserCreated(message);
    }
}
