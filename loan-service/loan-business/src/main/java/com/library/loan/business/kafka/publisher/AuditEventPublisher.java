package com.library.loan.business.kafka.publisher;

import com.library.loan.business.kafka.event.AuditEventMessage;

/**
 * Interface for publishing audit events to Kafka.
 * Provides methods for publishing audit events to default or specific topics.
 */
public interface AuditEventPublisher {

    /**
     * Publishes an audit event to the default audit topic.
     * @param event the audit event message to publish
     */
    void publishEvent(AuditEventMessage event);

    /**
     * Publishes an audit event to a specific topic.
     * @param topic the Kafka topic to publish to
     * @param event the audit event message to publish
     */
    void publishEvent(String topic, AuditEventMessage event);
}