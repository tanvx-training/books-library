package com.library.catalog.framework.kafka;


public interface AuditEventPublisher {

    void publishEvent(AuditEventMessage event);

    void publishEvent(String topic, AuditEventMessage event);
}