package com.library.notification.controller;

import com.library.common.aop.annotation.Loggable;
import com.library.common.constants.EventType;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
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
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "Notification",
        logArguments = true,
        logReturnValue = false, // Void method
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 5000L, // Email processing can take time
        messagePrefix = "NOTIFICATION_EVENT_LISTENER",
        customTags = {
            "layer=controller", 
            "messaging=true", 
            "kafka_consumer=true",
            "event_processing=true",
            "user_created_event=true",
            "notification_trigger=true",
            "async_processing=true"
        }
    )
    public void notifyUserCreated(@Payload UserCreatedMessage message) {
        log.info("Received user created event: {}", message);
        notificationService.handleUserCreated(message);
    }
}
