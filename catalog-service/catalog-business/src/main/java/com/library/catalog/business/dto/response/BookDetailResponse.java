package com.library.catalog.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Detailed response DTO for book data with copies information.
 * Contains comprehensive book information including all book copies.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDetailResponse {

    private UUID id; // Using publicId as the main ID for external API
    private String title;
    private String author; // Concatenated author names
    private String isbn;
    private String category; // Concatenated category names
    private String status; // Overall book status based on copies
    private List<BookCopyInfo> copies;
    private String publisher;
    private Short publicationYear;
    private String description;
    private String language;
    private Integer pages;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Book copy information for detailed book response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookCopyInfo {
        private UUID id; // Using publicId as the main ID for external API
        private String copyNumber;
        private String status;
        private String condition;
        private String location;
        private String barcode; // Generated from copy number for now
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}