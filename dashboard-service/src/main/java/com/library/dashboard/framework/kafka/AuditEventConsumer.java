package com.library.dashboard.framework.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dashboard.service.AuditLogService;
import com.library.dashboard.dto.response.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(
            topics = "#{T(java.util.Arrays).asList('${audit.kafka.topics}'.split(','))}",
            properties = {"spring.json.value.default.type=com.library.dashboard.framework.kafka.AuditEventMessage"})
    public void handleAuditEvent(AuditEventMessage eventMessage) {
        AuditLogResponse savedAuditLog = auditLogService.createAuditLog(eventMessage);
    }
}