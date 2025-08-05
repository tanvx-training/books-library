package com.library.member.controller.rest;

import com.library.member.business.UserManagementService;
import com.library.member.business.dto.request.UpdateUserRequest;
import com.library.member.business.dto.request.UserSearchCriteria;
import com.library.member.business.dto.response.UserResponse;

import com.library.member.repository.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserManagementService userManagementService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        UserResponse userResponse = userManagementService.getCurrentUserProfile();
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@Valid @RequestBody UpdateUserRequest updateRequest) {
        UserResponse userResponse = userManagementService.updateCurrentUserProfile(updateRequest);
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        UserRole userRole = null;
        if (StringUtils.hasText(role)) {
            userRole = UserRole.valueOf(role.toUpperCase());
        }

        UserSearchCriteria criteria = UserSearchCriteria.builder()
                .page(page)
                .size(size)
                .searchTerm(searchTerm)
                .role(userRole)
                .isActive(isActive)
                .sortBy(sortBy)
                .sortDirection(sortDirection)
                .build();

        Page<UserResponse> users = userManagementService.getAllUsers(criteria);
        return ResponseEntity.ok(users);

    }
}