package com.library.catalog.business.kafka.publisher;

import com.library.catalog.business.kafka.event.AuditEventMessage;

public interface AuditEventPublisher {

    void publishEvent(AuditEventMessage event);

    void publishEvent(String topic, AuditEventMessage event);
}