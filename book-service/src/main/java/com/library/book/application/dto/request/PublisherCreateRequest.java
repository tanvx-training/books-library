package com.library.book.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublisherCreateRequest {

    @NotBlank(message = "Publisher name must not be blank")
    @Size(max = 256, message = "Publisher name must not exceed 256 characters")
    private String name;

    private String address;
}