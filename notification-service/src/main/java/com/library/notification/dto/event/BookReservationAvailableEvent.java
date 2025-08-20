package com.library.notification.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookReservationAvailableEvent extends LibraryEvent {
    
    private UUID bookPublicId;
    private String bookTitle;
    private String bookIsbn;
    private UUID reservationPublicId;
    private LocalDate availableUntil;
}