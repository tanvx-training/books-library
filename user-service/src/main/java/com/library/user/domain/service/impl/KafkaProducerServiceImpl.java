package com.library.user.domain.service.impl;

import com.library.common.model.KafkaEvent;
import com.library.user.domain.service.KafkaProducerService;
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
    public <T> void createAndSendEvent(String topic, String key, String eventType, String source, T payload) {
        KafkaEvent<T> event = KafkaEvent.create(eventType, source, payload);
        this.sendEvent(topic, key, event);
    }
}
