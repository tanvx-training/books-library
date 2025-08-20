package com.library.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PublisherInfo {
        private UUID publicId;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private UUID publicId;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryInfo {
        private UUID publicId;
        private String name;
    }
}