package com.library.user.controller;

import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
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
    public ResponseEntity<PageResponseDTO<UserResponseDTO>> getAllUsers(@Valid @ModelAttribute PageRequestDTO pageRequestDTO) {
        return ResponseEntity.ok(userService.getAllUsers(pageRequestDTO));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDetailResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
}
