package com.library.catalog.business.dto.request;

import com.library.catalog.repository.enums.BookCopyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing book copy.
 * Contains validation annotations to ensure data integrity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookCopyRequest {

    @NotBlank(message = "Copy number is required")
    @Size(max = 255, message = "Copy number must not exceed 255 characters")
    private String copyNumber;

    @NotNull(message = "Status is required")
    private BookCopyStatus status;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
}