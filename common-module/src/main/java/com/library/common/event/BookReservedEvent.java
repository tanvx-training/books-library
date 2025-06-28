package com.library.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Payload for BOOK_RESERVED events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookReservedEvent {
    private Long userId;
    private String userEmail;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private String bookAuthor;
    private String isbn;
    private LocalDateTime reservationTime;
    private LocalDate estimatedAvailabilityDate;
    private int queuePosition;
} 