package com.library.user.interfaces.rest;

import com.library.user.application.dto.request.PaginatedRequest;
import com.library.user.application.dto.request.UserCreateRequest;
import com.library.user.application.dto.request.UserUpdateRequest;
import com.library.user.application.dto.response.ApiResponse;
import com.library.user.application.dto.response.PaginatedResponse;
import com.library.user.application.dto.response.UserDetailResponse;
import com.library.user.application.dto.response.UserResponse;
import com.library.user.application.service.UserApplicationService;
import com.library.user.infrastructure.enums.LogLevel;
import com.library.user.infrastructure.enums.OperationType;
import com.library.user.infrastructure.logging.Loggable;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserRestController {

    private final UserApplicationService userApplicationService;

    @GetMapping
    @Loggable(
            level = LogLevel.BASIC,
            operationType = OperationType.READ,
            resourceType = "User",
            logReturnValue = false,
            messagePrefix = "USER_API_LIST",
            customTags = {"layer=interface", "endpoint=getAllUsers", "pagination=true"}
    )
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponse>>> getAllUsers(@Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(userApplicationService.getAllUsers(paginatedRequest)));
    }

    @GetMapping("/{userId}")
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "User",
            performanceThresholdMs = 500L,
            messagePrefix = "USER_API_DETAIL",
            customTags = {"layer=interface", "endpoint=getUserById", "single_resource=true"}
    )
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(userApplicationService.getUserById(userId)));
    }

    @PostMapping
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.CREATE,
            resourceType = "User",
            performanceThresholdMs = 2000L,
            messagePrefix = "USER_API_CREATE",
            customTags = {"layer=interface", "endpoint=createUser"}
    )
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userApplicationService.createUser(request)));
    }

    @PutMapping("/{userId}")
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.UPDATE,
            resourceType = "User",
            performanceThresholdMs = 2000L,
            messagePrefix = "USER_API_UPDATE",
            customTags = {"layer=interface", "endpoint=updateUser"}
    )
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Long userId, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userApplicationService.updateUser(userId, request)));
    }
} 