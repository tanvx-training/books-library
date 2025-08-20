package com.library.catalog.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    @Min(value = 0, message = "Page number must be non-negative")
    @Builder.Default
    private Integer page = DEFAULT_PAGE;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = MAX_SIZE, message = "Page size must not exceed " + MAX_SIZE)
    @Builder.Default
    private Integer size = DEFAULT_SIZE;

    @Builder.Default
    private String sortBy = "title";

    @Builder.Default
    private String order = "asc";

    private String title;

    private String isbn;

    private String publisherName;

    private String authorName;

    private String categoryName;

    private Short publicationYear;

    private String language;

    public Pageable toPageable() {
        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(sortBy)) {
            sort = Sort.by(sortBy);
            if ("desc".equalsIgnoreCase(order)) {
                sort = sort.descending();
            } else {
                sort = sort.ascending();
            }
        }
        return PageRequest.of(page, size, sort);
    }
}
