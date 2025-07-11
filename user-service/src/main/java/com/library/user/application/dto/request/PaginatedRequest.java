package com.library.user.application.dto.request;

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
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 100;

    @Min(value = 0, message = "Page number cannot be less than 0")
    @Builder.Default
    private Integer page = DEFAULT_PAGE;

    @Min(value = 1, message = "Page size cannot be less than 1")
    @Max(value = MAX_SIZE, message = "Page size cannot be greater than " + MAX_SIZE)
    @Builder.Default
    private Integer size = DEFAULT_SIZE;

    private String sortBy;
    private String sortDirection;

    public Pageable toPageable() {
        Sort sort = Sort.unsorted();
        if (StringUtils.hasText(sortBy)) {
            sort = Sort.by(sortBy);
            if (Objects.nonNull(sortDirection) && sortDirection.equalsIgnoreCase("desc")) {
                sort = sort.descending();
            } else {
                sort = sort.ascending();
            }
        }
        return PageRequest.of(page, size, sort);
    }
} 