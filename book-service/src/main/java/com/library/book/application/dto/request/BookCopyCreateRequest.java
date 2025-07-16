package com.library.book.application.dto.request;

import com.library.book.domain.model.bookcopy.BookCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for creating a book copy
 */
@Data
public class BookCopyCreateRequest {
    
    @NotNull(message = "Book ID is required")
    private Long bookId;
    
    @NotBlank(message = "Copy number is required")
    @Size(max = 20, message = "Copy number cannot exceed 20 characters")
    private String copyNumber;
    
    private BookCondition condition = BookCondition.GOOD;
    
    @Size(max = 50, message = "Location cannot exceed 50 characters")
    private String location;
}