package com.library.catalog.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(max = 256, message = "Category name must not exceed 256 characters")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Size(max = 256, message = "Category slug must not exceed 256 characters")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Category slug must be URL-friendly (lowercase letters, numbers, and hyphens only)")
    private String slug;

    @Size(max = 5000, message = "Category description must not exceed 5000 characters")
    private String description;
}