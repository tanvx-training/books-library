package com.library.catalog.business.dto.request;

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
    @Size(max = 256, message = "Publisher name must not exceed 256 characters")
    private String name;

    @Size(max = 5000, message = "Address must not exceed 5000 characters")
    private String address;
}