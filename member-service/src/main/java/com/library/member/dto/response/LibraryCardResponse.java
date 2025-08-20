package com.library.member.dto.response;

import com.library.member.repository.LibraryCardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for library card information.
 * Contains all the necessary information about a library card for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryCardResponse {

    /**
     * Public identifier for the library card
     */
    private UUID publicId;

    /**
     * Unique card number for the library card
     */
    private String cardNumber;

    /**
     * Public identifier of the user who owns this card
     */
    private UUID userPublicId;

    /**
     * Date when the card was issued
     */
    private LocalDate issueDate;

    /**
     * Date when the card expires
     */
    private LocalDate expiryDate;

    /**
     * Current status of the library card
     */
    private LibraryCardStatus status;

    /**
     * Timestamp when the card was created
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the card was last updated
     */
    private LocalDateTime updatedAt;

    /**
     * Keycloak ID of the user who created the card
     */
    private String createdBy;

    /**
     * Keycloak ID of the user who last updated the card
     */
    private String updatedBy;
}