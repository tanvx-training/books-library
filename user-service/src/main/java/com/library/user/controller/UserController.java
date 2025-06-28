package com.library.user.controller;

import com.library.common.dto.ApiResponse;
import com.library.common.dto.PaginatedRequest;
import com.library.common.dto.PaginatedResponse;
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
    public ResponseEntity<ApiResponse<PaginatedResponse<UserResponseDTO>>> getAllUsers(@Valid @ModelAttribute PaginatedRequest paginatedRequest) {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(paginatedRequest)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
