package com.library.user.domain.model.librarycard;

import com.library.user.domain.event.LibraryCardCreatedEvent;
import com.library.user.domain.event.LibraryCardRenewedEvent;
import com.library.user.domain.model.shared.AggregateRoot;
import com.library.user.domain.model.user.UserId;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LibraryCard extends AggregateRoot {
    private LibraryCardId id;
    private UserId userId;
    private CardNumber cardNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private CardStatus status;

    // Private constructor for factory methods
    private LibraryCard() {}

    // Factory method for creating a new library card
    public static LibraryCard create(
            UserId userId,
            CardNumber cardNumber,
            LocalDate issueDate,
            LocalDate expiryDate) {

        LibraryCard card = new LibraryCard();
        card.id = LibraryCardId.createNew();
        card.userId = userId;
        card.cardNumber = cardNumber;
        card.issueDate = issueDate;
        card.expiryDate = expiryDate;
        card.status = CardStatus.ACTIVE;

        card.registerEvent(new LibraryCardCreatedEvent(card));

        return card;
    }

    // Method to renew a library card
    public void renew(LocalDate newExpiryDate) {
        this.expiryDate = newExpiryDate;
        registerEvent(new LibraryCardRenewedEvent(this));
    }

    // Method to deactivate a library card
    public void deactivate() {
        this.status = CardStatus.INACTIVE;
    }

    // Method to check if a card is expired
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    // For JPA/ORM reconstruction
    public static LibraryCard reconstitute(
            LibraryCardId id,
            UserId userId,
            CardNumber cardNumber,
            LocalDate issueDate,
            LocalDate expiryDate,
            CardStatus status) {

        LibraryCard card = new LibraryCard();
        card.id = id;
        card.userId = userId;
        card.cardNumber = cardNumber;
        card.issueDate = issueDate;
        card.expiryDate = expiryDate;
        card.status = status;

        return card;
    }
}