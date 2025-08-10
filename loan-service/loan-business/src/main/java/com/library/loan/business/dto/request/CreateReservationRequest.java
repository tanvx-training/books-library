package com.library.loan.business.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {

    @NotBlank(message = "Book public ID is required")
    private String bookPublicId;

    @NotBlank(message = "User public ID is required")
    private String userPublicId;

    @Future(message = "Expiry date must be in the future")
    private LocalDateTime expiryDate;
}