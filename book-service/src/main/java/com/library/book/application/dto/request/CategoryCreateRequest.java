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
    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 256, message = "Tên thể loại không được vượt quá 256 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 256, message = "Slug không được vượt quá 256 ký tự")
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug chỉ được chứa chữ thường, số và dấu gạch ngang")
    private String slug;

    private String description;
}