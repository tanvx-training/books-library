package com.library.catalog.dto.request;

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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookRequest {

    @NotBlank(message = "Book title is required")
    @Size(max = 255, message = "Book title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "ISBN is required")
    @Size(max = 50, message = "ISBN must not exceed 50 characters")
    private String isbn;

    @NotNull(message = "Publication year is required")
    @Min(value = 1, message = "Publication year must be a positive number")
    @Max(value = 2100, message = "Publication year cannot be in the far future")
    private Short publicationYear;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    @Size(max = 20, message = "Language must not exceed 20 characters")
    private String language;

    @Positive(message = "Number of pages must be a positive number")
    private Integer numberOfPages;

    @Size(max = 1000, message = "Cover image URL must not exceed 1000 characters")
    private String coverImageUrl;

    @NotNull(message = "Publisher public ID is required")
    private UUID publisherPublicId;

    private List<UUID> authorPublicIds;

    private List<UUID> categoryPublicIds;
}