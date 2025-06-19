package com.library.notification.presentation.controller;

import com.library.common.constants.EventType;
import com.library.notification.domain.service.NotificationService;
import com.library.notification.presentation.dto.UserCreatedMessage;
import lombok.RequiredArgsConstructor;
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
            properties = {"spring.json.value.default.type=com.library.notification.presentation.dto.UserCreatedMessage"}
    )
    public void notifyUserCreated(@Payload UserCreatedMessage message) {
        log.info("Received user created event: {}", message);
        
        try {
            notificationService.handleUserCreated(message);
            log.info("Successfully processed user created event for user ID: {}", 
                    message.getPayload() != null ? message.getPayload().getUserId() : "unknown");
        } catch (Exception e) {
            log.error("Error processing user created event: {}", e.getMessage(), e);
        }
    }
}
