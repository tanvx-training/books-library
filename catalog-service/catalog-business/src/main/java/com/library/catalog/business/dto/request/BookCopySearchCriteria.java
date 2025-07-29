package com.library.catalog.business.dto.request;

import com.library.catalog.repository.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Search criteria DTO for book copy search operations.
 * Contains various search parameters for filtering book copies.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopySearchCriteria {

    private Long bookId;
    private String copyNumber;
    private BookCopyStatus status;
    private String location;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private LocalDateTime updatedAtFrom;
    private LocalDateTime updatedAtTo;
}