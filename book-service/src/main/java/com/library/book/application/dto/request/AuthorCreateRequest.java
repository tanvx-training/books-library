package com.library.book.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCreateRequest {

    @NotBlank(message = "Author name must not be blank")
    @Size(max = 100, message = "Author name must not exceed 100 characters")
    private String name;

    private String biography;
}
