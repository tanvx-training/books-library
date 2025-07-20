package com.library.dashboard.controller.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.dashboard.business.AuditLogBusiness;
import com.library.dashboard.business.dto.event.AuditEventMessage;
import com.library.dashboard.business.dto.response.AuditLogResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditLogBusiness auditLogBusiness;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @KafkaListener(
            topics = "#{T(java.util.Arrays).asList('${audit.kafka.topics}'.split(','))}",
            properties = {"spring.json.value.default.type=com.library.dashboard.business.dto.event.AuditEventMessage"})
    public void handleAuditEvent(AuditEventMessage eventMessage) {
        AuditLogResponse savedAuditLog = auditLogBusiness.createAuditLog(eventMessage);
    }
}