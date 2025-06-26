package com.library.user.presentation.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LibraryCardResponseDTO {

    private Long id;
    private String cardNumber;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String status;
}
