package com.library.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Payload for CARD_RENEWED events
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardRenewedEvent {
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String cardNumber;
    private LocalDate previousExpiryDate;
    private LocalDate newExpiryDate;
    private String cardType;
} 