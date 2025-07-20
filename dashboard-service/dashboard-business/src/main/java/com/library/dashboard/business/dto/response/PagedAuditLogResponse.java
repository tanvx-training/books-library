package com.library.dashboard.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for paginated author results.
 * Contains author data along with pagination metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedAuditLogResponse {

    // Getters and Setters
    private List<AuditLogResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}