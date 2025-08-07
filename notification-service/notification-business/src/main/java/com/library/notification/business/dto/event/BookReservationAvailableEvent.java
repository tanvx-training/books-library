package com.library.notification.business.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Event fired when a reserved book becomes available
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BookReservationAvailableEvent extends LibraryEvent {
    
    private UUID bookPublicId;
    private String bookTitle;
    private String bookIsbn;
    private UUID reservationPublicId;
    private LocalDate availableUntil;
}