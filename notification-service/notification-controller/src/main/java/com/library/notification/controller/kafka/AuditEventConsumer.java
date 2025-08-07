package com.library.notification.controller.kafka;

import com.library.notification.business.NotificationPreferencesService;
import com.library.notification.business.dto.event.AuditEventMessage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final NotificationPreferencesService notificationPreferencesService;

    @SneakyThrows
    @KafkaListener(
            topics = "#{T(java.util.Arrays).asList('${audit.kafka.topics}'.split(','))}",
            properties = {"spring.json.value.default.type=com.library.notification.business.dto.event.AuditEventMessage"})
    public void handleAuditEvent(AuditEventMessage eventMessage) {
        log.info("Audit event received: {}", eventMessage);
        notificationPreferencesService.createDefaultPreferences(UUID.fromString(eventMessage.getEntityId()));
    }
}