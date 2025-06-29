package com.library.user.service.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.common.model.KafkaEvent;
import com.library.user.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "KafkaEvent",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 3000L,
        messagePrefix = "KAFKA_PRODUCER_SEND_EVENT",
        customTags = {
            "layer=service", 
            "messaging=true", 
            "async_operation=true",
            "event_publishing=true",
            "kafka_producer=true"
        }
    )
    public <T> void sendEvent(String topic, String key, KafkaEvent<T> event) {

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, key, event);
        log.info("Sending event to topic {}: {}", topic, event);

        future.whenComplete((result, ex) -> {
            if (Objects.isNull(ex)) {
                RecordMetadata recordMetadata = result.getRecordMetadata();
                log.info("Sent event {} to topic {}, partition {}, offset {}",
                        event.getEventId(),
                        recordMetadata.topic(),
                        recordMetadata.partition(),
                        recordMetadata.offset());
            } else {
                log.error("Failed to send event {} to topic {}", event.getEventId(), topic, ex);
            }
        });
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "KafkaEvent",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        performanceThresholdMs = 3500L,
        messagePrefix = "KAFKA_PRODUCER_CREATE_AND_SEND",
        customTags = {
            "layer=service", 
            "messaging=true", 
            "event_creation=true",
            "event_publishing=true",
            "kafka_producer=true",
            "composite_operation=true"
        }
    )
    public <T> void createAndSendEvent(String topic, String key, String eventType, String source, T payload) {
        KafkaEvent<T> event = KafkaEvent.create(eventType, source, payload);
        this.sendEvent(topic, key, event);
    }
}
