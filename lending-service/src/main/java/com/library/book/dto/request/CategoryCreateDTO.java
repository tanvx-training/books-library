package com.library.book.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CategoryCreateDTO {

    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(max = 256, message = "Tên thể loại không được vượt quá 256 ký tự")
    private String name;

    @NotBlank(message = "Slug không được để trống")
    @Size(max = 256, message = "Slug không được vượt quá 256 ký tự")
    private String slug;

    private String description;
}

