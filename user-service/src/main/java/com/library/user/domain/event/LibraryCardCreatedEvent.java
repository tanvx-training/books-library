package com.library.user.domain.event;

import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.shared.DomainEvent;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LibraryCardCreatedEvent extends DomainEvent {
    private final Long id;
    private final Long userId;
    private final String cardNumber;
    private final LocalDate issueDate;
    private final LocalDate expiryDate;
    private final String status;

    public LibraryCardCreatedEvent(LibraryCard libraryCard) {
        this.id = libraryCard.getId().getValue();
        this.userId = libraryCard.getUserId().getValue();
        this.cardNumber = libraryCard.getCardNumber().getValue();
        this.issueDate = libraryCard.getIssueDate();
        this.expiryDate = libraryCard.getExpiryDate();
        this.status = libraryCard.getStatus().name();
    }
}