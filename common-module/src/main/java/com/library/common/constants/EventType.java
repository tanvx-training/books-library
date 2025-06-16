package com.library.common.constants;

public class EventType {
    // User-related events
    public static final String USER_CREATED = "user-created";
    public static final String USER_UPDATED = "user-updated";
    public static final String CARD_CREATED = "card-created";
    public static final String CARD_RENEWED = "card-renewed";
    public static final String CARD_EXPIRED = "card-expired";
    public static final String CARD_EXPIRING_SOON = "card-expiring-soon";
    public static final String PASSWORD_RESET = "password-reset";

    // Book-related events
    public static final String BOOK_BORROWED = "book-borrowed";
    public static final String BOOK_RETURNED = "book-returned";
    public static final String BOOK_OVERDUE = "book-overdue";
    public static final String BOOK_RESERVED = "book-reserved";
    public static final String RESERVATION_AVAILABLE = "reservation-available";
}