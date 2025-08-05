package com.library.catalog.business.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for updating a book with copies.
 * This allows complete update of book information and copy management.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookWithCopiesRequest {

    // Book Information
    @NotBlank(message = "Book title is required")
    @Size(max = 255, message = "Book title must not exceed 255 characters")
    private String title;

    @NotNull(message = "Author IDs are required")
    @Size(min = 1, message = "At least one author is required")
    private List<Long> authorIds; // List of author internal IDs

    @NotBlank(message = "ISBN is required")
    @Size(max = 50, message = "ISBN must not exceed 50 characters")
    private String isbn;

    @NotNull(message = "Category IDs are required")
    @Size(min = 1, message = "At least one category is required")
    private List<Long> categoryIds; // List of category internal IDs

    private Long publisherId; // Publisher internal ID (optional)

    @NotNull(message = "Publication year is required")
    @Min(value = 1, message = "Publication year must be a positive number")
    @Max(value = 2100, message = "Publication year cannot be in the far future")
    private Short publicationYear;

    @Size(max = 20, message = "Language must not exceed 20 characters")
    private String language;

    @Positive(message = "Number of pages must be a positive number")
    private Integer pages;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    // Copy Management
    @Valid
    @Size(min = 1, message = "At least one copy is required")
    private List<BookCopyUpdateRequest> copies;

    /**
     * Request DTO for book copy update information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookCopyUpdateRequest {

        private UUID id; // Copy public ID for existing copies (null for new copies)

        @NotBlank(message = "Copy number is required")
        @Size(max = 20, message = "Copy number must not exceed 20 characters")
        private String copyNumber;

        @NotBlank(message = "Physical condition is required")
        private String condition; // excellent, good, fair, poor, damaged

        @NotBlank(message = "Shelf location is required")
        @Size(max = 50, message = "Shelf location must not exceed 50 characters")
        private String location;

        @Size(max = 100, message = "Barcode must not exceed 100 characters")
        private String barcode; // Optional

        @Size(max = 1000, message = "Notes must not exceed 1000 characters")
        private String notes; // Optional

        private Boolean deleted = false; // Mark copy for deletion
    }
}