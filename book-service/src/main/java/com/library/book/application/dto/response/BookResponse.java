package com.library.book.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Schema(
        name = "BookResponse",
        description = "Complete book information including metadata, authors, publisher, and categories"
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {
    
    @Schema(description = "Unique identifier of the book", example = "1")
    private Long id;
    
    @Schema(description = "Title of the book", example = "The Great Gatsby")
    private String title;
    
    @Schema(description = "International Standard Book Number", example = "978-0-7432-7356-5")
    private String isbn;
    
    @Schema(description = "Year the book was published", example = "1925")
    private Integer publicationYear;
    
    @Schema(description = "Detailed description of the book", example = "A classic American novel set in the Jazz Age")
    private String description;
    
    @Schema(description = "URL to the book's cover image", example = "https://example.com/covers/great-gatsby.jpg")
    private String coverImageUrl;
    
    @Schema(description = "Publisher information")
    private PublisherResponse publisher;
    
    @Schema(description = "List of authors who wrote this book")
    private List<AuthorResponse> authors;
    
    @Schema(description = "List of categories this book belongs to")
    private List<CategoryResponse> categories;
    
    @Schema(description = "Timestamp when the book was created", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "Timestamp when the book was last updated", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;
}
