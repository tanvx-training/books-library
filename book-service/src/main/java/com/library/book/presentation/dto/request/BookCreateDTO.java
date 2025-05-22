package com.library.book.presentation.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookCreateDTO {
    @NotBlank(message = "ISBN không được để trống")
    @Size(max = 256, message = "ISBN không được vượt quá 256 ký tự")
    private String isbn;

    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 1000, message = "Tiêu đề không được vượt quá 1000 ký tự")
    private String title;

    @NotBlank(message = "Tác giả không được để trống")
    @Size(max = 256, message = "Tên tác giả không được vượt quá 256 ký tự")
    private String author;

    private Integer publicationYear;

    @Size(max = 256, message = "Tên nhà xuất bản không được vượt quá 256 ký tự")
    private String publisher;

    private String imageUrlS;
    private String imageUrlM;
    private String imageUrlL;

    @Min(value = 0, message = "Số lượng sách khả dụng không được âm")
    private Integer availableCopies;

    @Min(value = 0, message = "Tổng số sách không được âm")
    private Integer totalCopies;

    @NotNull(message = "ID thể loại không được để trống")
    private UUID categoryId;
}
