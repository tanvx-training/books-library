package com.library.book.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.library.book.utils.annotation.ValidYear;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookCreateDTO {


    @NotBlank(message = "Tiêu đề không được để trống")
    @Size(max = 1000, message = "Tiêu đề không được vượt quá 1000 ký tự")
    private String title;

    @NotBlank(message = "ISBN không được để trống")
    @Size(max = 256, message = "ISBN không được vượt quá 256 ký tự")
    private String isbn;

    @NotNull(message = "Nhà xuất bản không được để trống")
    private Long publisherId;

    @ValidYear
    private Integer publicationYear;
    private String description;
    private String coverImageUrl;

    @NotEmpty(message = "Tác giả không được để trống")
    private List<Long> authors;

    @NotEmpty(message = "Thể loại không được để trống")
    private List<Long> categories;

    private List<BookCopyRequestDTO> bookCopies;
}
