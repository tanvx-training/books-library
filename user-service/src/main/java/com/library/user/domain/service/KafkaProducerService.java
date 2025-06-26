package com.library.user.domain.service;

import com.library.common.model.KafkaEvent;

public interface KafkaProducerService {

    <T> void sendEvent(String topic, String key, KafkaEvent<T> event);

    <T> void createAndSendEvent(String topic, String key, String eventType, String source, T payload);
}
