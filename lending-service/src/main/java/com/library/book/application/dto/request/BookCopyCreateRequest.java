package com.library.book.application.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookCopyCreateRequest {
    @NotNull(message = "Book ID cannot be empty")
    private Long bookId;
    
    @NotBlank(message = "Book title cannot be empty")
    private String bookTitle;
    
    @NotBlank(message = "Copy number cannot be empty")
    private String copyNumber;
    
    private String condition;
    private String location;
    private String status;
} 