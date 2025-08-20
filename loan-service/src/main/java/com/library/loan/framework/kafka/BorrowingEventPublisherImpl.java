package com.library.loan.framework.kafka;

import com.library.loan.framework.kafka.BorrowingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implementation of BorrowingEventPublisher for publishing borrowing lifecycle events to Kafka.
 * Handles the actual Kafka publishing with proper error handling and logging.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowingEventPublisherImpl implements BorrowingEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${borrowing.kafka.topic:borrowing-events}")
    private String defaultTopic;

    @Override
    public void publishEvent(BorrowingEvent event) {
        publishEvent(defaultTopic, event);
    }

    @Override
    public void publishEvent(String topic, BorrowingEvent event) {
        if (event == null || event.getBorrowingPublicId() == null) {
            log.warn("Invalid borrowing event, skipping publication: {}", event);
            return;
        }

        try {
            log.debug("Publishing borrowing event to topic {}: {}", topic, event);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event.getEventId(), event);
            
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Failed to publish borrowing event to topic {}: {}", topic, throwable.getMessage(), throwable);
                } else {
                    log.debug("Successfully published borrowing event to topic {} with offset: {}", 
                             topic, result.getRecordMetadata().offset());
                }
            });

        } catch (Exception e) {
            log.error("Error publishing borrowing event to topic {}: {}", topic, e.getMessage(), e);
        }
    }
}