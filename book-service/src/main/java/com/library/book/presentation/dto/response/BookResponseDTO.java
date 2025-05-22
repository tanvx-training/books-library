package com.library.book.presentation.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookResponseDTO {
    private UUID id;
    private String isbn;
    private String title;
    private String author;
    private Integer publicationYear;
    private String publisher;
    private String imageUrlS;
    private String imageUrlM;
    private String imageUrlL;
    private Integer availableCopies;
    private Integer totalCopies;
    private CategoryResponseDTO category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

