package com.library.user.controller;

import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
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
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.CREATE,
        resourceType = "User",
        logArguments = true,
        logReturnValue = false, // Don't log response to avoid sensitive info leakage
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        sanitizeSensitiveData = true,
        messagePrefix = "USER_REGISTRATION",
        customTags = {"endpoint=register", "security_operation=true"}
    )
    public ResponseEntity<ApiResponse<RegisterResponseDTO>> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(authService.registerUser(registerRequestDTO)));
    }

    @PostMapping("/login")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.AUTHENTICATION,
        resourceType = "UserSession",
        logArguments = true,
        logReturnValue = false, // Don't log tokens
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        sanitizeSensitiveData = true,
        messagePrefix = "USER_AUTHENTICATION",
        customTags = {"endpoint=login", "security_operation=true", "auth_type=credentials"}
    )
    public ResponseEntity<ApiResponse<LoginResponseDTO>> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(ApiResponse.success(authService.loginUser(loginRequestDTO)));
    }
}
