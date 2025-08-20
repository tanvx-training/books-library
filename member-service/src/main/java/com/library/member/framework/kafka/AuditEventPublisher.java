package com.library.member.framework.kafka;

import com.library.member.framework.kafka.AuditEventMessage;

public interface AuditEventPublisher {

    void publishEvent(AuditEventMessage event);

    void publishEvent(String topic, AuditEventMessage event);
}