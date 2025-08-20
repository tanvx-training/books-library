package com.library.loan.dto.request;

import com.library.loan.repository.BorrowingStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BorrowingSearchRequest implements Serializable {

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
    private String sortBy = "borrowDate";

    @Builder.Default
    private String order = "desc";

    private String keyword;

    private BorrowingStatus status;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate borrowDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate borrowDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDateTo;

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