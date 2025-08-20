package com.library.catalog.dto.request;

import com.library.catalog.aop.EntityValidationException;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorSearchRequest implements Serializable {

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

    @Size(min = 1, max = 100, message = "Name filter must be between 1 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-.,']+$", message = "Name filter contains invalid characters")
    private String name;

    @Pattern(regexp = "^(name|createdAt|updatedAt)$", message = "Sort field must be one of: name, createdAt, updatedAt")
    @Builder.Default
    private String sortBy = "name";

    @Pattern(regexp = "^(asc|desc)$", message = "Sort order must be either 'asc' or 'desc'")
    @Builder.Default
    private String order = "asc";

    public Pageable toPageable() {
        Sort sort = Sort.by(validateSortField(sortBy));
        if ("desc".equalsIgnoreCase(order)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        return PageRequest.of(page, size, sort);
    }

    private String validateSortField(String sortField) {
        // Define allowed sort fields for Author entity
        return switch (sortField.toLowerCase()) {
            case "name" -> "name";
            case "biography" -> "biography";
            case "created_at" -> "createdAt";
            case "updated_at" -> "updatedAt";
            case "created_by" -> "createdBy";
            case "updated_by" -> "updatedBy";
            default -> throw EntityValidationException.invalidField("Author", "sortBy", sortField,
                    "Valid sort fields are: name, biography, createdAt, updatedAt, createdBy, updatedBy");
        };
    }
}
