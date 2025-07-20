package com.library.catalog.business.kafka.publisher;

import com.library.catalog.business.kafka.event.AuditEventMessage;

/**
 * Interface for publishing audit events to Kafka topics.
 * This interface provides a contract for sending audit events to the message broker.
 */
public interface AuditEventPublisher {

    /**
     * Publishes an audit event to the configured Kafka topic.
     *
     * @param event The audit event message to publish
     * @throws RuntimeException if the event cannot be published
     */
    void publishEvent(AuditEventMessage event);

    /**
     * Publishes an audit event to a specific Kafka topic.
     *
     * @param topic The topic to publish to
     * @param event The audit event message to publish
     * @throws RuntimeException if the event cannot be published
     */
    void publishEvent(String topic, AuditEventMessage event);
}