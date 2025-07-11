package com.library.user.domain.event;

import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.shared.DomainEvent;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LibraryCardRenewedEvent extends DomainEvent {
    private final Long id;
    private final Long userId;
    private final String cardNumber;
    private final LocalDate previousExpiryDate;
    private final LocalDate newExpiryDate;

    public LibraryCardRenewedEvent(LibraryCard libraryCard) {
        this.id = libraryCard.getId().getValue();
        this.userId = libraryCard.getUserId().getValue();
        this.cardNumber = libraryCard.getCardNumber().getValue();
        // In a real implementation, you would track the previous expiry date
        this.previousExpiryDate = null;
        this.newExpiryDate = libraryCard.getExpiryDate();
    }
}