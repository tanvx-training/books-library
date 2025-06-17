package com.library.common.enums;

/**
 * Enum representing the different statuses of a notification
 */
public enum NotificationStatus {
    SENT,       // Notification has been sent
    DELIVERED,  // Notification has been delivered (if delivery confirmation is available)
    READ,       // Notification has been read by the user
    FAILED      // Notification failed to send
} 