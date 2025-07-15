package com.library.book.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryCreateRequest {

    @NotBlank(message = "Category name must not be blank")
    @Size(max = 256, message = "Category name must not exceed 256 characters")
    private String name;

    @NotBlank(message = "Slug must not be blank")
    @Size(max = 256, message = "Slug must not exceed 256 characters")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug may only contain lowercase letters, numbers, and hyphens")
    private String slug;

    private String description;
}