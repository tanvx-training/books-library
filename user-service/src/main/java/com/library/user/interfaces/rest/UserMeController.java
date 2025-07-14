package com.library.user.interfaces.rest;

import com.library.user.application.dto.response.ApiResponse;
import com.library.user.application.dto.response.UserDetailResponse;
import com.library.user.application.mapper.UserDtoMapper;
import com.library.user.application.service.UserSyncService;
import com.library.user.domain.model.user.User;
import com.library.user.infrastructure.enums.LogLevel;
import com.library.user.infrastructure.enums.OperationType;
import com.library.user.infrastructure.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for endpoints related to the current authenticated user
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserMeController {

    private final UserSyncService userSyncService;
    private final UserDtoMapper userDtoMapper;

    /**
     * Get the current authenticated user's information
     * This endpoint also synchronizes the user data from Keycloak
     */
    @GetMapping("/me")
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "User",
            performanceThresholdMs = 500L,
            messagePrefix = "USER_API_ME",
            customTags = {"layer=interface", "endpoint=getCurrentUser"}
    )
    public ResponseEntity<ApiResponse<UserDetailResponse>> getCurrentUser() {
        // Synchronize the user from Keycloak and get the user data
        User user = userSyncService.syncCurrentUser();
        
        // Convert to DTO and return
        UserDetailResponse response = userDtoMapper.toUserDetailResponse(user);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
} 