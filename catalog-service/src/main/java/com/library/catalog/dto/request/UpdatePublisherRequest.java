package com.library.catalog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePublisherRequest {

    @NotBlank(message = "Publisher name is required")
    @Size(max = 100, message = "Publisher name must not exceed 100 characters")
    private String name;

    private String address;
}