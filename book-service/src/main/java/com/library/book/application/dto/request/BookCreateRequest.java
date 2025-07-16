package com.library.book.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(
        name = "BookCreateRequest",
        description = "Request object for creating a new book in the library system"
)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateRequest {

    @Schema(
            description = "Title of the book",
            example = "The Great Gatsby",
            required = true,
            maxLength = 200
    )
    @NotBlank(message = "Title must not be blank")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @Schema(
            description = "International Standard Book Number (ISBN)",
            example = "978-0-7432-7356-5",
            required = true,
            maxLength = 20
    )
    @NotBlank(message = "ISBN must not be blank")
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;

    @Schema(
            description = "Unique identifier of the publisher",
            example = "1",
            required = true
    )
    @NotNull(message = "Publisher must not be null")
    private Long publisherId;

    @Schema(
            description = "Year the book was published",
            example = "1925",
            minimum = "1000",
            maximum = "2100"
    )
    private Integer publicationYear;

    @Schema(
            description = "Detailed description or summary of the book",
            example = "A classic American novel set in the Jazz Age, exploring themes of wealth, love, and the American Dream"
    )
    private String description;

    @Schema(
            description = "URL to the book's cover image",
            example = "https://example.com/covers/great-gatsby.jpg"
    )
    private String coverImageUrl;

    @Schema(
            description = "List of author IDs associated with this book",
            example = "[1, 2]",
            required = true
    )
    @NotEmpty(message = "At least one author must be specified")
    private List<Long> authorIds;

    @Schema(
            description = "List of category IDs for book classification",
            example = "[1, 3]",
            required = true
    )
    @NotEmpty(message = "At least one category must be specified")
    private List<Long> categoryIds;
} 