package com.library.user.controller;

import com.library.common.dto.ApiResponse;
import com.library.user.service.AuthService;
import com.library.user.dto.request.LoginRequestDTO;
import com.library.user.dto.request.RegisterRequestDTO;
import com.library.user.dto.response.LoginResponseDTO;
import com.library.user.dto.response.RegisterResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(authService.registerUser(registerRequestDTO)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(authService.loginUser(loginRequestDTO)));
    }
}
