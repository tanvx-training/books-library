package com.library.member.business.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Request DTO for creating a new library card.
 * Contains the necessary information to create a library card for a user.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLibraryCardRequest {

    /**
     * Keycloak ID of the user for whom the card is being created
     */
    @NotBlank(message = "User Keycloak ID is required")
    private String userKeycloakId;

    /**
     * Expiry date for the library card
     * Must be in the future
     */
    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    private LocalDate expiryDate;
}