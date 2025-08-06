package com.library.member.business.impl;

import com.library.member.business.UserManagementService;
import com.library.member.business.dto.request.CreateUserRequest;
import com.library.member.business.dto.request.UpdateUserRequest;
import com.library.member.business.dto.request.UserSearchCriteria;
import com.library.member.business.dto.response.UserResponse;
import com.library.member.business.dto.sync.UserSyncRequest;
import com.library.member.business.aop.exception.EntityNotFoundException;
import com.library.member.business.aop.exception.EntityValidationException;
import com.library.member.business.mapper.UserMapper;
import com.library.member.business.security.AuthenticatedUser;
import com.library.member.business.aop.exception.AuthenticationException;
import com.library.member.business.aop.exception.AuthorizationException;
import com.library.member.business.security.UnifiedAuthenticationService;
import com.library.member.repository.UserRepository;
import com.library.member.repository.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementServiceImpl implements UserManagementService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UnifiedAuthenticationService authenticationService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile() {
        try {
            // Get current authenticated user
            AuthenticatedUser authenticatedUser = authenticationService.getCurrentUser();
            if (authenticatedUser == null) {
                throw new AuthenticationException("No authenticated user context available");
            }

            String keycloakId = authenticatedUser.getKeycloakId();

            // Try to find user in database
            UserEntity userEntity = userRepository.findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                    .orElse(null);

            UserResponse response;
            if (userEntity != null) {
                // User exists in database, return full profile
                response = userMapper.toUserResponse(userEntity);
            } else {
                // User not in database, return profile from authentication context
                response = userMapper.toUserResponse(authenticatedUser, null);
            }
            return response;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error retrieving current user profile", e);
            throw new RuntimeException("Failed to retrieve current user profile", e);
        }
    }

    @Override
    @Transactional
    public UserResponse syncUserFromKeycloak(String keycloakId, UserSyncRequest syncRequest) {

        String currentUserKeycloakId = authenticationService.getCurrentUserKeycloakId();
        try {
            // Validate input
            validateSyncRequest(keycloakId, syncRequest);

            // Check if user already exists
            UserEntity existingUser = userRepository.findByKeycloakId(keycloakId).orElse(null);

            UserEntity userEntity;
            boolean isNewUser = false;

            if (existingUser != null) {
                // Update existing user
                userMapper.updateEntityFromSyncRequest(existingUser, syncRequest);
                existingUser.setUpdatedBy(currentUserKeycloakId);
                existingUser.setUpdatedAt(LocalDateTime.now());

                // Restore user if it was soft deleted
                if (existingUser.getDeletedAt() != null) {
                    existingUser.setDeletedAt(null);
                    log.info("Restored soft-deleted user during sync: {}", keycloakId);
                }

                userEntity = userRepository.save(existingUser);
            } else {
                // Create new user
                userEntity = userMapper.toEntity(syncRequest);
                userEntity.setCreatedBy(currentUserKeycloakId);
                userEntity.setUpdatedBy(currentUserKeycloakId);
                userEntity = userRepository.save(userEntity);
                isNewUser = true;
            }

            UserResponse response = userMapper.toUserResponse(userEntity);

            log.info("User synchronization completed for keycloakId: {} ({})",
                    keycloakId, isNewUser ? "created" : "updated");
            return response;
        } catch (EntityValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during user synchronization for keycloakId: {}", keycloakId, e);
            throw new RuntimeException("Failed to synchronize user from Keycloak", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByKeycloakId(String keycloakId) {
        String currentUserKeycloakId = authenticationService.getCurrentUserKeycloakId();

        try {
            if (!StringUtils.hasText(keycloakId)) {
                throw new EntityValidationException("Keycloak ID cannot be null or empty");
            }

            UserEntity userEntity = userRepository.findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + keycloakId));

            return userMapper.toUserResponse(userEntity);

        } catch (EntityNotFoundException | EntityValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error retrieving user by Keycloak ID: {}", keycloakId, e);
            throw new RuntimeException("Failed to retrieve user by Keycloak ID", e);
        }
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUserProfile(UpdateUserRequest updateRequest) {
        try {
            // Get current authenticated user
            AuthenticatedUser authenticatedUser = authenticationService.getCurrentUser();
            if (authenticatedUser == null) {
                throw new AuthenticationException("No authenticated user context available");
            }

            String keycloakId = authenticatedUser.getKeycloakId();

            // Validate update request
            validateUpdateRequest(updateRequest);

            // Find user in database
            UserEntity userEntity = userRepository.findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + keycloakId));

            // Update user entity
            userMapper.updateEntityFromUpdateRequest(userEntity, updateRequest);
            userEntity.setUpdatedBy(keycloakId);
            userEntity.setUpdatedAt(LocalDateTime.now());

            userEntity = userRepository.save(userEntity);

            return userMapper.toUserResponse(userEntity);

        } catch (AuthenticationException | EntityNotFoundException | EntityValidationException e) {
           throw e;
        } catch (Exception e) {
            log.error("Unexpected error updating current user profile", e);
            throw new RuntimeException("Failed to update current user profile", e);
        }
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest createRequest) {

        String currentUserKeycloakId = authenticationService.getCurrentUserKeycloakId();

        try {
            // Check admin privileges
            if (!authenticationService.hasRole("ADMIN")) {
                throw new AuthorizationException("Admin privileges required to create users");
            }

            // Validate create request
            validateCreateRequest(createRequest);

            // Check if user already exists
            if (userRepository.existsByKeycloakIdAndDeletedAtIsNull(createRequest.getKeycloakId())) {
                throw new EntityValidationException("User already exists with Keycloak ID: " + createRequest.getKeycloakId());
            }

            if (userRepository.existsByEmailAndDeletedAtIsNull(createRequest.getEmail())) {
                throw new EntityValidationException("User already exists with email: " + createRequest.getEmail());
            }

            if (StringUtils.hasText(createRequest.getUsername()) &&
                    userRepository.existsByUsernameAndDeletedAtIsNull(createRequest.getUsername())) {
                throw new EntityValidationException("User already exists with username: " + createRequest.getUsername());
            }

            // Create new user entity
            UserEntity userEntity = userMapper.toEntity(createRequest);
            userEntity.setCreatedBy(currentUserKeycloakId);
            userEntity.setUpdatedBy(currentUserKeycloakId);

            userEntity = userRepository.save(userEntity);

            return userMapper.toUserResponse(userEntity);

        } catch (AuthorizationException | EntityValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error creating user", e);
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByPublicId(UUID publicId) {
        String currentUserKeycloakId = authenticationService.getCurrentUserKeycloakId();

        try {
            if (publicId == null) {
                throw new EntityValidationException("Public ID cannot be null");
            }

            UserEntity userEntity = userRepository.findByPublicIdAndDeletedAtIsNull(publicId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with public ID: " + publicId));

            return userMapper.toUserResponse(userEntity);

        } catch (EntityNotFoundException | EntityValidationException e) {
           throw e;
        } catch (Exception e) {
            log.error("Unexpected error retrieving user by public ID: {}", publicId, e);
            throw new RuntimeException("Failed to retrieve user by public ID", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(UserSearchCriteria criteria) {
        String currentUserKeycloakId = authenticationService.getCurrentUserKeycloakId();

        try {
            // Check admin privileges
            if (!authenticationService.hasRole("ADMIN")) {
                throw new AuthorizationException("Admin privileges required to list users");
            }

            // Validate and set defaults for criteria
            if (criteria == null) {
                criteria = UserSearchCriteria.builder().build();
            }

            // Create pageable
            Sort sort = Sort.by(
                    "desc".equalsIgnoreCase(criteria.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
                    criteria.getSortBy()
            );
            Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize(), sort);

            // Execute search based on criteria
            Page<UserEntity> userEntities;

            if (StringUtils.hasText(criteria.getSearchTerm())) {
                // Search by name or email
                userEntities = userRepository.findByNameContaining(criteria.getSearchTerm(), pageable);
            } else if (criteria.getRole() != null && criteria.getIsActive() != null) {
                // Filter by role and active status
                userEntities = userRepository.findByRoleAndIsActiveAndDeletedAtIsNull(
                        criteria.getRole(), criteria.getIsActive(), pageable);
            } else if (criteria.getRole() != null) {
                // Filter by role only
                userEntities = userRepository.findByRoleAndDeletedAtIsNull(criteria.getRole(), pageable);
            } else if (criteria.getIsActive() != null) {
                // Filter by active status only
                userEntities = userRepository.findByIsActiveAndDeletedAtIsNull(criteria.getIsActive(), pageable);
            } else {
                // No filters, get all users
                userEntities = userRepository.findByDeletedAtIsNull(pageable);
            }

            // Convert to response DTOs
            return userEntities.map(userMapper::toUserResponse);

        } catch (AuthorizationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error retrieving users list", e);
            throw new RuntimeException("Failed to retrieve users list", e);
        }
    }

    private void validateSyncRequest(String keycloakId, UserSyncRequest syncRequest) {
        if (!StringUtils.hasText(keycloakId)) {
            throw new EntityValidationException("Keycloak ID cannot be null or empty");
        }

        if (syncRequest == null) {
            throw new EntityValidationException("User sync request cannot be null");
        }

        if (!keycloakId.equals(syncRequest.getKeycloakId())) {
            throw new EntityValidationException("Keycloak ID in path does not match request body");
        }

        if (!StringUtils.hasText(syncRequest.getEmail())) {
            throw new EntityValidationException("Email is required for user synchronization");
        }

        if (!StringUtils.hasText(syncRequest.getFirstName())) {
            throw new EntityValidationException("First name is required for user synchronization");
        }

        if (!StringUtils.hasText(syncRequest.getLastName())) {
            throw new EntityValidationException("Last name is required for user synchronization");
        }

        // Validate email format (basic validation)
        if (!syncRequest.getEmail().contains("@")) {
            throw new EntityValidationException("Invalid email format");
        }
    }

    /**
     * Validates the user update request data.
     */
    private void validateUpdateRequest(UpdateUserRequest updateRequest) {
        if (updateRequest == null) {
            throw new EntityValidationException("Update request cannot be null");
        }

        // At least one field must be provided for update
        if (updateRequest.getFirstName() == null &&
                updateRequest.getLastName() == null &&
                updateRequest.getPhoneNumber() == null &&
                updateRequest.getAddress() == null &&
                updateRequest.getDateOfBirth() == null) {
            throw new EntityValidationException("At least one field must be provided for update");
        }

        // Validate field lengths if provided
        if (updateRequest.getFirstName() != null && updateRequest.getFirstName().trim().isEmpty()) {
            throw new EntityValidationException("First name cannot be empty");
        }

        if (updateRequest.getLastName() != null && updateRequest.getLastName().trim().isEmpty()) {
            throw new EntityValidationException("Last name cannot be empty");
        }

        if (updateRequest.getPhoneNumber() != null && updateRequest.getPhoneNumber().length() > 20) {
            throw new EntityValidationException("Phone number must not exceed 20 characters");
        }

        if (updateRequest.getAddress() != null && updateRequest.getAddress().length() > 500) {
            throw new EntityValidationException("Address must not exceed 500 characters");
        }
    }

    /**
     * Validates the user creation request data.
     */
    private void validateCreateRequest(CreateUserRequest createRequest) {
        if (createRequest == null) {
            throw new EntityValidationException("Create request cannot be null");
        }

        if (!StringUtils.hasText(createRequest.getKeycloakId())) {
            throw new EntityValidationException("Keycloak ID is required");
        }

        if (!StringUtils.hasText(createRequest.getEmail())) {
            throw new EntityValidationException("Email is required");
        }

        if (!StringUtils.hasText(createRequest.getFirstName())) {
            throw new EntityValidationException("First name is required");
        }

        if (!StringUtils.hasText(createRequest.getLastName())) {
            throw new EntityValidationException("Last name is required");
        }

        if (createRequest.getRole() == null) {
            throw new EntityValidationException("User role is required");
        }

        // Validate email format (basic validation)
        if (!createRequest.getEmail().contains("@")) {
            throw new EntityValidationException("Invalid email format");
        }

        // Validate field lengths
        if (createRequest.getFirstName().length() > 100) {
            throw new EntityValidationException("First name must not exceed 100 characters");
        }

        if (createRequest.getLastName().length() > 100) {
            throw new EntityValidationException("Last name must not exceed 100 characters");
        }

        if (StringUtils.hasText(createRequest.getUsername()) && createRequest.getUsername().length() > 50) {
            throw new EntityValidationException("Username must not exceed 50 characters");
        }

        if (StringUtils.hasText(createRequest.getPhoneNumber()) && createRequest.getPhoneNumber().length() > 20) {
            throw new EntityValidationException("Phone number must not exceed 20 characters");
        }

        if (StringUtils.hasText(createRequest.getAddress()) && createRequest.getAddress().length() > 500) {
            throw new EntityValidationException("Address must not exceed 500 characters");
        }
    }
}