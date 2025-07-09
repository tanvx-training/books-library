package com.library.book.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookResponse {
    private Long id;
    private String title;
    private String isbn;
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;
    private PublisherResponse publisher;
    private List<AuthorResponse> authors;
    private List<CategoryResponse> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
