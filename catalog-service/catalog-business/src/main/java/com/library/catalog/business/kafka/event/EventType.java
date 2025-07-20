package com.library.catalog.business.kafka.event;

/**
 * Enumeration of audit event types for entity operations.
 * This enum provides a standardized way to categorize different types of events
 * that can occur on entities within the system.
 */
public enum EventType {
    CREATED("CREATED"),
    UPDATED("UPDATED"),
    DELETED("DELETED");

    private final String value;

    EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}