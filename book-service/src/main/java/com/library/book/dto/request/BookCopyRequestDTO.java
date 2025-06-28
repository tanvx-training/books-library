package com.library.book.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookCopyRequestDTO {
    @NotNull(message = "Book ID không được để trống")
    private Long bookId;
    @NotBlank(message = "Copy number không được để trống")
    private String copyNumber;
    private String condition;
    private String location;
    private String status;
}
