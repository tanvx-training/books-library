package com.library.book.application.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookCopyResponse {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String copyNumber;
    private String status;
    private String condition;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 