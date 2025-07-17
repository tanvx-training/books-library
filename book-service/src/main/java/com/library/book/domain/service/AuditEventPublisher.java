package com.library.book.domain.service;

import com.library.book.domain.model.shared.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    @Value("${spring.application.name}")
    private String applicationName;

    public void publishAuditEvent(AuditEvent event) {
        String topic = applicationName + "-audit-logs";
        String key = event.getEntityType() + "-" + event.getEntityId();
        
        log.info("Publishing audit event to topic {}: {}", topic, event);
        
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.debug("Audit event sent successfully: {}", result.getRecordMetadata());
            } else {
                log.error("Failed to send audit event: {}", event, ex);
            }
        });
    }
}