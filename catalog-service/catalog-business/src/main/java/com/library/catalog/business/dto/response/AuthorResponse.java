package com.library.catalog.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for author data.
 * Contains all relevant author information for API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorResponse {

    private Integer id;
    private String name;
    private String biography;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}