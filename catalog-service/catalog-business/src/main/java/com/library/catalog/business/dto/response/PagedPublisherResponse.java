package com.library.catalog.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for paginated publisher results.
 * Contains publisher data along with pagination metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedPublisherResponse {

    private List<PublisherResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}