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
    @NotBlank(message = "Tên nhà xuất bản không được để trống")
    @Size(max = 256, message = "Tên nhà xuất bản không được vượt quá 256 ký tự")
    private String name;

    private String address;
}