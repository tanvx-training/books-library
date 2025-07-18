package com.library.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Payload for CARD_CREATED events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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