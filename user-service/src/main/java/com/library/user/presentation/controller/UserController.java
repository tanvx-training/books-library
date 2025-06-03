package com.library.user.presentation.controller;

import com.library.common.dto.PageRequestDTO;
import com.library.common.dto.PageResponseDTO;
import com.library.user.domain.service.UserService;
import com.library.user.presentation.dto.response.UserResponseDTO;
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
}
