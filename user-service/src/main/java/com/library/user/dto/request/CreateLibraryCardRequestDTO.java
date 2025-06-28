package com.library.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateLibraryCardRequestDTO {

    @NotNull(message = "User ID là bắt buộc")
    private Long userId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @NotNull(message = "Ngày hết hạn là bắt buộc")
    @Future(message = "Ngày hết hạn phải lớn hơn ngày hôm nay")
    private LocalDate expiryDate;
}
