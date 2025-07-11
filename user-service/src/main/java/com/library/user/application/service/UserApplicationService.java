package com.library.user.application.service;

import com.library.user.application.dto.request.PaginatedRequest;
import com.library.user.application.dto.request.UserCreateRequest;
import com.library.user.application.dto.request.UserUpdateRequest;
import com.library.user.application.dto.response.LibraryCardResponse;
import com.library.user.application.dto.response.PaginatedResponse;
import com.library.user.application.dto.response.UserDetailResponse;
import com.library.user.application.dto.response.UserResponse;
import com.library.user.application.exception.UserApplicationException;
import com.library.user.application.mapper.UserDtoMapper;
import com.library.user.domain.exception.InvalidUserDataException;
import com.library.user.domain.exception.UserNotFoundException;
import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.user.Role;
import com.library.user.domain.model.user.User;
import com.library.user.domain.model.user.UserId;
import com.library.user.domain.repository.LibraryCardRepository;
import com.library.user.domain.repository.UserRepository;
import com.library.user.domain.service.UserDomainService;
import com.library.user.infrastructure.enums.LogLevel;
import com.library.user.infrastructure.enums.OperationType;
import com.library.user.infrastructure.event.DomainEventPublisher;
import com.library.user.infrastructure.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final LibraryCardRepository libraryCardRepository;
    private final UserDtoMapper userDtoMapper;
    private final DomainEventPublisher eventPublisher;

    /**
     * Get all users with pagination
     */
    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "User",
            logReturnValue = false,
            performanceThresholdMs = 800L,
            messagePrefix = "USER_APP_SERVICE_LIST",
            customTags = {
                    "layer=application",
                    "transaction=readonly",
                    "pagination=true"
            }
    )
    public PaginatedResponse<UserResponse> getAllUsers(PaginatedRequest paginatedRequest) {
        try {
            Pageable pageable = paginatedRequest.toPageable();
            List<User> users = userRepository.findAll(pageable.getPageNumber(), pageable.getPageSize());

            List<UserResponse> userResponses = users.stream()
                    .map(userDtoMapper::toUserResponse)
                    .collect(Collectors.toList());

            long totalUsers = userRepository.count();
            Page<UserResponse> page = new PageImpl<>(userResponses, pageable, totalUsers);

            return PaginatedResponse.from(page);
        } catch (Exception e) {
            throw new UserApplicationException("Failed to get users: " + e.getMessage(), e);
        }
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.READ,
            resourceType = "User",
            includeInPerformanceMonitoring = true,
            performanceThresholdMs = 300L,
            messagePrefix = "USER_APP_SERVICE_DETAIL",
            customTags = {
                    "layer=application",
                    "transaction=readonly",
                    "single_entity=true"
            }
    )
    public UserDetailResponse getUserById(Long userId) {
        try {
            User user = userRepository.findById(UserId.of(userId))
                    .orElseThrow(() -> new UserNotFoundException(userId));

            List<LibraryCard> libraryCards = libraryCardRepository.findByUserId(user.getId());
            List<LibraryCardResponse> libraryCardResponses = libraryCards.stream()
                    .map(userDtoMapper::toLibraryCardResponse)
                    .collect(Collectors.toList());

            return userDtoMapper.toUserDetailResponse(user, libraryCardResponses);
        } catch (UserNotFoundException e) {
            throw new UserApplicationException("User not found with ID: " + userId, e);
        } catch (Exception e) {
            throw new UserApplicationException("Failed to get user: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new user
     */
    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.CREATE,
            resourceType = "User",
            includeInPerformanceMonitoring = true,
            performanceThresholdMs = 2000L,
            messagePrefix = "USER_APP_SERVICE_CREATE",
            customTags = {
                    "layer=application",
                    "transaction=write",
                    "user_management=true"
            }
    )
    public UserResponse createUser(UserCreateRequest request) {
        try {
            // Convert roles from strings to Role objects
            Set<Role> roles = new HashSet<>();
            if (request.getRoles() != null) {
                roles = request.getRoles().stream()
                        .map(Role::of)
                        .collect(Collectors.toSet());
            }

            User user = userDomainService.createUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhone(),
                    roles
            );

            User savedUser = userRepository.save(user);

            // Publish domain events
            savedUser.clearEvents();

            return userDtoMapper.toUserResponse(savedUser);
        } catch (InvalidUserDataException e) {
            throw new UserApplicationException("Invalid user data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new UserApplicationException("Failed to create user: " + e.getMessage(), e);
        }
    }

    /**
     * Update user information
     */
    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.UPDATE,
            resourceType = "User",
            includeInPerformanceMonitoring = true,
            performanceThresholdMs = 2000L,
            messagePrefix = "USER_APP_SERVICE_UPDATE",
            customTags = {
                    "layer=application",
                    "transaction=write",
                    "user_management=true"
            }
    )
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        try {
            User user = userDomainService.updateUser(
                    userId,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhone()
            );

            User savedUser = userRepository.save(user);

            // Publish domain events
            savedUser.clearEvents();

            return userDtoMapper.toUserResponse(savedUser);
        } catch (UserNotFoundException e) {
            throw new UserApplicationException("User not found with ID: " + userId, e);
        } catch (InvalidUserDataException e) {
            throw new UserApplicationException("Invalid user data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new UserApplicationException("Failed to update user: " + e.getMessage(), e);
        }
    }
}