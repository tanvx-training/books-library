package com.library.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Payload for CARD_EXPIRING_SOON events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardExpiringSoonEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String cardNumber;
    private LocalDate expiryDate;
    private String cardType;
    private int daysUntilExpiry;
    private String renewalInstructions;
} 