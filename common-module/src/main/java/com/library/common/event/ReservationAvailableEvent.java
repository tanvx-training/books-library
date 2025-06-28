package com.library.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Payload for RESERVATION_AVAILABLE events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationAvailableEvent {
    private Long userId;
    private String userEmail;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String isbn;
    private LocalDateTime reservationTime;
    private LocalDateTime availableUntil;
    private String pickupLocation;
} 