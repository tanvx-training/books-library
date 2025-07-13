package com.library.user.domain.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Event payload for card creation events that will be sent to Kafka
 */
@Data
@Builder
public class CardCreatedEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String cardNumber;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String cardType;
}