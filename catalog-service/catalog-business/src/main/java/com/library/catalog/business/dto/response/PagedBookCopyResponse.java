package com.library.catalog.business.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for paginated book copy results.
 * Contains book copy data along with pagination metadata.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedBookCopyResponse {

    private List<BookCopyResponse> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}