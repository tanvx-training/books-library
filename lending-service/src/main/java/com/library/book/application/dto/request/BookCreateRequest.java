package com.library.book.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookCreateRequest {

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 200, message = "Tiêu đề không được vượt quá 200 ký tự")
    private String title;

    @NotBlank(message = "ISBN không được để trống")
    @Size(max = 20, message = "ISBN không được vượt quá 20 ký tự")
    private String isbn;

    @NotNull(message = "Nhà xuất bản không được để trống")
    private Long publisherId;

    private Integer publicationYear;
    private String description;
    private String coverImageUrl;

    @NotEmpty(message = "Tác giả không được để trống")
    private List<Long> authorIds;

    @NotEmpty(message = "Thể loại không được để trống")
    private List<Long> categoryIds;
} 