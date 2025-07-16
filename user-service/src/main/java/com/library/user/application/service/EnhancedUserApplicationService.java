package com.library.user.application.service;

import com.library.user.application.dto.request.UserCreateRequest;
import com.library.user.application.dto.response.UserResponse;
import com.library.user.application.exception.UserApplicationException;
import com.library.user.application.mapper.UserDtoMapper;
import com.library.user.domain.event.UserRegisteredEvent;
import com.library.user.domain.event.UserSuspendedEvent;
import com.library.user.domain.exception.DomainException;
import com.library.user.domain.exception.InvalidUserDataException;
import com.library.user.domain.exception.UserNotFoundException;
import com.library.user.domain.factory.EnhancedUserFactory;
import com.library.user.domain.model.user.User;
import com.library.user.domain.model.user.UserId;
import com.library.user.domain.repository.UserRepository;
import com.library.user.domain.service.UserDomainService;
import com.library.user.domain.specification.ActiveUserSpecification;
import com.library.user.domain.specification.EligibleForLibraryCardSpecification;
import com.library.user.domain.specification.UserSpecification;
import com.library.user.infrastructure.event.DomainEventPublisher;
import com.library.user.infrastructure.logging.Loggable;
import com.library.user.infrastructure.enums.LogLevel;
import com.library.user.infrastructure.enums.OperationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced User Application Service with DDD patterns
 * Orchestrates user-related use cases using domain services and specifications
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EnhancedUserApplicationService {

    private final UserDomainService userDomainService;
    private final EnhancedUserFactory userFactory;
    private final UserRepository userRepository;
    private final DomainEventPublisher eventPublisher;
    private final UserDtoMapper userDtoMapper;
    
    // Specifications
    private final ActiveUserSpecification activeUserSpec;
    private final EligibleForLibraryCardSpecification libraryCardEligibilitySpec;

    /**
     * Register a new user with comprehensive validation
     */
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "User",
        messagePrefix = "USER_REGISTRATION",
        customTags = {"operation=register", "layer=application"}
    )
    public UserResponse registerUser(UserCreateRequest request) {
        try {
            log.info("Starting user registration for username: {}", request.getUsername());
            
            // Create user using enhanced factory
            EnhancedUserFactory.UserCreationRequest creationRequest = 
                new EnhancedUserFactory.UserCreationRequest(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPhone()
                );
            
            User user = userFactory.createStandardUser(creationRequest);
            
            // Save user
            User savedUser = userRepository.save(user);
            
            // Publish registration event
            UserRegisteredEvent registrationEvent = new UserRegisteredEvent(savedUser, "STANDARD");
            eventPublisher.publish(registrationEvent);
            
            // Clear domain events
            savedUser.clearEvents();
            
            log.info("User registration completed successfully for ID: {}", savedUser.getId().getValue());
            
            return userDtoMapper.toUserResponse(savedUser);
            
        } catch (InvalidUserDataException e) {
            log.warn("User registration failed due to invalid data: {}", e.getMessage());
            throw new UserApplicationException("Registration failed: " + e.getMessage(), e);
        } catch (DomainException e) {
            log.error("Domain error during user registration: {}", e.getMessage());
            throw new UserApplicationException("Registration failed due to business rule violation", e);
        } catch (Exception e) {
            log.error("Unexpected error during user registration", e);
            throw new UserApplicationException("Registration failed due to system error", e);
        }
    }

    /**
     * Register a Keycloak user
     */
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "User",
        messagePrefix = "KEYCLOAK_USER_REGISTRATION"
    )
    public UserResponse registerKeycloakUser(String keycloakId, String username, String email,
                                           String firstName, String lastName, String phone) {
        try {
            log.info("Starting Keycloak user registration for ID: {}", keycloakId);
            
            EnhancedUserFactory.KeycloakUserInfo keycloakInfo = 
                new EnhancedUserFactory.KeycloakUserInfo(
                    keycloakId, username, email, firstName, lastName, phone
                );
            
            User user = userFactory.createKeycloakUser(keycloakInfo);
            User savedUser = userRepository.save(user);
            
            // Publish registration event
            UserRegisteredEvent registrationEvent = new UserRegisteredEvent(savedUser, "KEYCLOAK");
            eventPublisher.publish(registrationEvent);
            
            savedUser.clearEvents();
            
            log.info("Keycloak user registration completed for ID: {}", savedUser.getId().getValue());
            
            return userDtoMapper.toUserResponse(savedUser);
            
        } catch (Exception e) {
            log.error("Error during Keycloak user registration", e);
            throw new UserApplicationException("Keycloak user registration failed", e);
        }
    }

    /**
     * Get active users using specification
     */
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "User",
        messagePrefix = "GET_ACTIVE_USERS"
    )
    public List<UserResponse> getActiveUsers() {
        try {
            List<User> activeUsers = userRepository.findBySpecification(activeUserSpec);
            
            return activeUsers.stream()
                .map(userDtoMapper::toUserResponse)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error retrieving active users", e);
            throw new UserApplicationException("Failed to retrieve active users", e);
        }
    }

    /**
     * Get users eligible for library card
     */
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "User",
        messagePrefix = "GET_LIBRARY_CARD_ELIGIBLE_USERS"
    )
    public List<UserResponse> getUsersEligibleForLibraryCard() {
        try {
            UserSpecification eligibleSpec = activeUserSpec.and(libraryCardEligibilitySpec);
            List<User> eligibleUsers = userRepository.findBySpecification(eligibleSpec);
            
            return eligibleUsers.stream()
                .map(userDtoMapper::toUserResponse)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error retrieving users eligible for library card", e);
            throw new UserApplicationException("Failed to retrieve eligible users", e);
        }
    }

    /**
     * Suspend a user with reason
     */
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "User",
        messagePrefix = "USER_SUSPENSION"
    )
    public void suspendUser(Long userId, String reason, String suspendedBy, String notes) {
        try {
            log.info("Starting user suspension for ID: {}", userId);
            
            User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));
            
            // Deactivate user
            user.deactivate();
            
            // Save user
            userRepository.save(user);
            
            // Publish suspension event
            UserSuspendedEvent.SuspensionReason suspensionReason = 
                UserSuspendedEvent.SuspensionReason.valueOf(reason.toUpperCase());
            
            UserSuspendedEvent suspensionEvent = new UserSuspendedEvent(
                user.getId(), suspensionReason, suspendedBy, notes
            );
            eventPublisher.publish(suspensionEvent);
            
            user.clearEvents();
            
            log.info("User suspension completed for ID: {}", userId);
            
        } catch (UserNotFoundException e) {
            log.warn("User not found for suspension: {}", userId);
            throw new UserApplicationException("User not found: " + userId, e);
        } catch (Exception e) {
            log.error("Error during user suspension", e);
            throw new UserApplicationException("User suspension failed", e);
        }
    }

    /**
     * Check if user can borrow books
     */
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "User",
        messagePrefix = "CHECK_BORROWING_ELIGIBILITY"
    )
    public boolean canUserBorrowBooks(Long userId) {
        try {
            User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException(userId));
            
            // Simple check - user must be active to borrow books
            return user.isActive();
            
        } catch (UserNotFoundException e) {
            log.warn("User not found for borrowing eligibility check: {}", userId);
            return false;
        } catch (Exception e) {
            log.error("Error checking borrowing eligibility", e);
            return false;
        }
    }

    /**
     * Update user profile information
     */
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "User",
        messagePrefix = "USER_PROFILE_UPDATE"
    )
    public UserResponse updateUserProfile(Long userId, String firstName, String lastName, String phone) {
        try {
            log.info("Starting profile update for user ID: {}", userId);
            
            User updatedUser = userDomainService.updateUser(userId, firstName, lastName, phone);
            
            // Publish events
            eventPublisher.publishAll(updatedUser.getDomainEvents());
            updatedUser.clearEvents();
            
            log.info("Profile update completed for user ID: {}", userId);
            
            return userDtoMapper.toUserResponse(updatedUser);
            
        } catch (UserNotFoundException e) {
            log.warn("User not found for profile update: {}", userId);
            throw new UserApplicationException("User not found: " + userId, e);
        } catch (Exception e) {
            log.error("Error during profile update", e);
            throw new UserApplicationException("Profile update failed", e);
        }
    }

    /**
     * Get user statistics using specifications
     */
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "User",
        messagePrefix = "GET_USER_STATISTICS"
    )
    public UserStatistics getUserStatistics() {
        try {
            long totalUsers = userRepository.count();
            long activeUsers = userRepository.countBySpecification(activeUserSpec);
            long eligibleForCard = userRepository.countBySpecification(
                activeUserSpec.and(libraryCardEligibilitySpec)
            );
            
            return new UserStatistics(totalUsers, activeUsers, eligibleForCard);
            
        } catch (Exception e) {
            log.error("Error retrieving user statistics", e);
            throw new UserApplicationException("Failed to retrieve user statistics", e);
        }
    }

    /**
     * User statistics DTO
     */
    public static class UserStatistics {
        private final long totalUsers;
        private final long activeUsers;
        private final long eligibleForLibraryCard;
        
        public UserStatistics(long totalUsers, long activeUsers, long eligibleForLibraryCard) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.eligibleForLibraryCard = eligibleForLibraryCard;
        }
        
        public long getTotalUsers() { return totalUsers; }
        public long getActiveUsers() { return activeUsers; }
        public long getEligibleForLibraryCard() { return eligibleForLibraryCard; }
    }
}