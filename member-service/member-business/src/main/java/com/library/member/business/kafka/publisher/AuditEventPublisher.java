package com.library.member.business.kafka.publisher;

import com.library.member.business.kafka.event.AuditEventMessage;

public interface AuditEventPublisher {

    void publishEvent(AuditEventMessage event);

    void publishEvent(String topic, AuditEventMessage event);
}