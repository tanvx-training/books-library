package com.library.user.presentation.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.library.user.domain.enums.LibraryCardStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateLibraryCardStatusRequestDTO {

    @NotNull(message = "Trạng thái thẻ là bắt buộc")
    private LibraryCardStatus status;
    
    private String reason;
} 