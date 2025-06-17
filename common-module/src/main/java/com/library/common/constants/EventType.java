package com.library.common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventType {
    // User-related events
    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String CARD_CREATED = "CARD_CREATED";
    public static final String CARD_RENEWED = "CARD_RENEWED";
    public static final String CARD_EXPIRED = "CARD_EXPIRED";
    public static final String CARD_EXPIRING_SOON = "CARD_EXPIRING_SOON";
    public static final String PASSWORD_RESET = "PASSWORD_RESET";

    // Book-related events
    public static final String BOOK_BORROWED = "BOOK_BORROWED";
    public static final String BOOK_RETURNED = "BOOK_RETURNED";
    public static final String BOOK_OVERDUE = "BOOK_OVERDUE";
    public static final String BOOK_RESERVED = "BOOK_RESERVED";
    public static final String RESERVATION_AVAILABLE = "RESERVATION_AVAILABLE";
}