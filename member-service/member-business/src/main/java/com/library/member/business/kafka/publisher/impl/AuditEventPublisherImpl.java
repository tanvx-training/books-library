package com.library.member.business.kafka.publisher.impl;

import com.library.member.business.kafka.event.AuditEventMessage;
import com.library.member.business.kafka.publisher.AuditEventPublisher;
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

    @Value("${audit.kafka.topic:member-service-audit-logs}")
    private String defaultTopic;

    @Override
    public void publishEvent(AuditEventMessage event) {
        publishEvent(defaultTopic, event);
    }

    @Override
    public void publishEvent(String topic, AuditEventMessage event) {
        try {
            log.debug("Publishing audit event to topic {}: {}", topic, event);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event.getEntityId(), event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.debug("Successfully published audit event to topic {} with offset: {}",
                            topic, result.getRecordMetadata().offset());
                } else {
                    log.error("Failed to publish audit event to topic {}: {}", topic, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("Error publishing audit event to topic {}: {}", topic, e.getMessage(), e);
            throw new RuntimeException("Failed to publish audit event", e);
        }
    }
}