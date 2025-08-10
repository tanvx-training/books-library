package com.library.loan.business.kafka.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {
    CREATED("CREATED"),
    UPDATED("UPDATED"),
    DELETED("DELETED"),
    RETURNED("RETURNED"),
    RENEWED("RENEWED"),
    ACCESSED("ACCESSED");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}