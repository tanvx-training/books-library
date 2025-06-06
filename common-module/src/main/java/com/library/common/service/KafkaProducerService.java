package com.library.common.service;

import com.library.common.enums.EventType;
import com.library.common.model.KafkaEvent;

public interface KafkaProducerService {

    <T> void sendEvent(String topic, String key, KafkaEvent<T> event);

    <T> KafkaEvent<T> createAndSendEvent(String topic, String key, EventType eventType, String source, T payload);
}
