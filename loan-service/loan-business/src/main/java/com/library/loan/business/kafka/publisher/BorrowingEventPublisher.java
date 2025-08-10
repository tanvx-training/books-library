package com.library.loan.business.kafka.publisher;

import com.library.loan.business.kafka.event.BorrowingEvent;

/**
 * Interface for publishing borrowing lifecycle events to Kafka.
 * Used for integration with other services that need to react to borrowing changes.
 */
public interface BorrowingEventPublisher {

    /**
     * Publishes a borrowing event to the default borrowing events topic.
     * @param event the borrowing event to publish
     */
    void publishEvent(BorrowingEvent event);

    /**
     * Publishes a borrowing event to a specific topic.
     * @param topic the Kafka topic to publish to
     * @param event the borrowing event to publish
     */
    void publishEvent(String topic, BorrowingEvent event);
}