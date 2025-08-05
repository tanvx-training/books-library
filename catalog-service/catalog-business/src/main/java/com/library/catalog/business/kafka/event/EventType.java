package com.library.catalog.business.kafka.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    CREATED("CREATED"),
    UPDATED("UPDATED"),
    DELETED("DELETED");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}