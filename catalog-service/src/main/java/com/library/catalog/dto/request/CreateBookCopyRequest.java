package com.library.catalog.dto.request;

import com.library.catalog.repository.BookCopyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookCopyRequest {

    @NotBlank(message = "Copy number is required")
    @Size(max = 255, message = "Copy number must not exceed 255 characters")
    private String copyNumber;

    @NotNull(message = "Status is required")
    private BookCopyStatus status;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;
}