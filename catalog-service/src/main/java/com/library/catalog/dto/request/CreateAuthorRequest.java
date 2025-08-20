package com.library.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAuthorRequest {

    @NotBlank(message = "Author name is required")
    @Size(max = 100, message = "Author name must not exceed 100 characters")
    private String name;

    @Size(max = 5000, message = "Biography must not exceed 5000 characters")
    private String biography;
}