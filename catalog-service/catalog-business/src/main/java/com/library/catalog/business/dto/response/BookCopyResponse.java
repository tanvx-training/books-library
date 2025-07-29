package com.library.catalog.business.dto.response;

import com.library.catalog.repository.enums.BookCopyStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for book copy data.
 * Contains all relevant book copy information including related book data for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCopyResponse {

    private Long id;
    private Long bookId;
    private String copyNumber;
    private BookCopyStatus status;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
    private BookInfo book;

    /**
     * Simplified book information for book copy response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookInfo {
        private Long id;
        private String title;
        private String isbn;
    }
}