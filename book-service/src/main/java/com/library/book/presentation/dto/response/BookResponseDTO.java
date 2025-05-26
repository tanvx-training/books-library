package com.library.book.presentation.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookResponseDTO {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;
    private PublisherResponseDTO publisher;
    private List<AuthorResponseDTO> authors;
    private List<CategoryResponseDTO> categories;
    private List<BookCopyResponseDTO> bookCopies;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

