package com.library.user.presentation.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterRequestDTO {

    @NotBlank(message = "Tên đăng nhập là bắt buộc")
    @Size(min = 3, max = 50, message = "Tên đăng nhập phải có độ dài từ 3 đến 50 ký tự")
    private String username;

    @NotBlank(message = "Mật khẩu là bắt buộc")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Email là bắt buộc")
    @Email(message = "Email phải hợp lệ")
    private String email;

    @Size(max = 50, message = "Tên phải ít hơn 50 ký tự")
    private String firstName;

    @Size(max = 50, message = "Họ phải ít hơn 50 ký tự")
    private String lastName;

    @Size(max = 20, message = "Số điện thoại phải ít hơn 20 ký tự")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,20}$", message = "Số điện thoại không hợp lệ")
    private String phone;
}
