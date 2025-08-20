package com.library.member.dto.request;

import com.library.member.repository.LibraryCardStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating library card status.
 * Contains the new status and optional reason for the status change.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCardStatusRequest {

    /**
     * New status for the library card
     */
    @NotNull(message = "Status is required")
    private LibraryCardStatus status;

    /**
     * Optional reason for the status change
     */
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}