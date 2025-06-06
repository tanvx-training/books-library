package com.library.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventType {

    // User-related events
    USER_CREATED("user-created"),
    USER_UPDATED("user-updated"),
    CARD_CREATED("card-created"),
    CARD_RENEWED("card-renewed"),
    CARD_EXPIRED("card-expired"),
    CARD_EXPIRING_SOON("card-expiring-soon"),
    PASSWORD_RESET("password-reset"),

    // Book-related events
    BOOK_BORROWED("book-borrowed"),
    BOOK_RETURNED("book-returned"),
    BOOK_OVERDUE("book-overdue"),
    BOOK_RESERVED("book-reserved"),
    RESERVATION_AVAILABLE("reservation-available");

    private final String topicName;

    // Utility method to get EventType from a topic name
    public static EventType fromTopicName(String topicName) {
        for (EventType event : EventType.values()) {
            if (event.topicName.equals(topicName)) {
                return event;
            }
        }
        throw new IllegalArgumentException("No EventType found for topic: " + topicName);
    }

    // Utility method to check if a topic name is valid
    public static boolean isValidTopic(String topicName) {
        for (EventType event : EventType.values()) {
            if (event.topicName.equals(topicName)) {
                return true;
            }
        }
        return false;
    }
}
