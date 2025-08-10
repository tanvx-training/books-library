package com.library.loan.business.kafka.publisher.impl;

import com.library.loan.business.kafka.event.AuditEventMessage;
import com.library.loan.business.kafka.publisher.AuditEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditEventPublisherImpl implements AuditEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${audit.kafka.topic:loan-service-audit-logs}")
    private String defaultTopic;

    @Override
    public void publishEvent(AuditEventMessage event) {
        publishEvent(defaultTopic, event);
    }

    @Override
    public void publishEvent(String topic, AuditEventMessage event) {
        if (event == null || !event.isValid()) {
            log.warn("Invalid audit event, skipping publication: {}", event);
            return;
        }

        try {
            log.debug("Publishing audit event to topic {}: {}", topic, event);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event.getEventId(), event);
            
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to publish audit event to topic {}: {}", topic, throwable.getMessage(), throwable);
                } else {
                    log.debug("Successfully published audit event to topic {} with offset: {}", 
                             topic, result.getRecordMetadata().offset());
                }
            });

        } catch (Exception e) {
            log.error("Error publishing audit event to topic {}: {}", topic, e.getMessage(), e);
        }
    }
}