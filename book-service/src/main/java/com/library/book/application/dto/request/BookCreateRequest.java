package com.library.book.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "Title must not be blank")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    @NotBlank(message = "ISBN must not be blank")
    @Size(max = 20, message = "ISBN must not exceed 20 characters")
    private String isbn;

    @NotNull(message = "Publisher ID must not be null")
    @Positive(message = "Publisher ID must be positive")
    private Long publisherId;

    @NotNull(message = "Publication year must not be null")
    @Positive(message = "Publication year must be positive")
    private Integer publicationYear;

    private String description;

    private String coverImageUrl;

    @NotEmpty(message = "At least one author must be specified")
    private List<Long> authorIds;

    @NotEmpty(message = "At least one category must be specified")
    private List<Long> categoryIds;
}