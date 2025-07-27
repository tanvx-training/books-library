package com.library.catalog.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response DTO for book data.
 * Contains all relevant book information including related entity data for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookResponse {

    private UUID publicId;
    private String title;
    private String isbn;
    private Short publicationYear;
    private String description;
    private String language;
    private Integer numberOfPages;
    private String coverImageUrl;
    private PublisherInfo publisher;
    private List<AuthorInfo> authors;
    private List<CategoryInfo> categories;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;

    /**
     * Simplified publisher information for book response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublisherInfo {
        private UUID publicId;
        private String name;
    }

    /**
     * Simplified author information for book response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private UUID publicId;
        private String name;
    }

    /**
     * Simplified category information for book response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private UUID publicId;
        private String name;
    }
}