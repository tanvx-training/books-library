package com.library.user.controller;

import com.library.common.aop.annotation.Loggable;
import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.user.service.UserService;
import com.library.user.dto.response.UserDetailResponseDTO;
import com.library.user.dto.response.UserResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = false, // Large result set - don't log full response
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "USER_LIST",
        customTags = {"endpoint=getAllUsers", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponseDTO>>> getAllUsers(@Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(paginatedRequest)));
    }

    @GetMapping("/{userId}")
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "USER_DETAIL",
        customTags = {"endpoint=getUserById", "single_resource=true"}
    )
    public ResponseEntity<UserDetailResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
