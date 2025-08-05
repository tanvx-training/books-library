package com.library.member.business.impl;

import com.library.member.business.LibraryCardService;
import com.library.member.business.dto.request.CreateLibraryCardRequest;
import com.library.member.business.dto.request.UpdateCardStatusRequest;
import com.library.member.business.dto.response.LibraryCardResponse;
import com.library.member.business.exception.AuthenticationException;
import com.library.member.business.exception.AuthorizationException;
import com.library.member.business.exception.EntityNotFoundException;
import com.library.member.business.exception.EntityValidationException;

import com.library.member.business.mapper.LibraryCardMapper;
import com.library.member.business.security.AuthenticatedUser;
import com.library.member.business.security.UnifiedAuthenticationService;
import com.library.member.business.util.CardNumberGenerator;
import com.library.member.repository.LibraryCardRepository;
import com.library.member.repository.UserRepository;
import com.library.member.repository.entity.LibraryCardEntity;
import com.library.member.repository.entity.UserEntity;
import com.library.member.repository.enums.LibraryCardStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryCardServiceImpl implements LibraryCardService {

    private final LibraryCardRepository libraryCardRepository;
    private final UserRepository userRepository;
    private final LibraryCardMapper libraryCardMapper;
    private final CardNumberGenerator cardNumberGenerator;
    private final UnifiedAuthenticationService authenticationService;

    @Override
    @Transactional
    public LibraryCardResponse createLibraryCard(CreateLibraryCardRequest request) {
        log.info("Creating library card for user: {}", request.getUserKeycloakId());

        // Validate authentication and authorization
        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();
        validateLibrarianOrAdminAccess(currentUser, "create library card");

        // Find the user for whom the card is being created
        UserEntity user = userRepository.findByKeycloakIdAndDeletedAtIsNull(request.getUserKeycloakId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + request.getUserKeycloakId()));

        // Validate that the user is active
        if (!user.getIsActive()) {
            throw new EntityValidationException("Cannot create library card for inactive user");
        }

        // Generate unique card number with retry logic
        String cardNumber = generateUniqueCardNumber();

        // Create the library card entity
        LibraryCardEntity cardEntity = libraryCardMapper.toEntity(request, user, cardNumber);

        try {
            // Save the library card
            LibraryCardEntity savedCard = libraryCardRepository.save(cardEntity);
            
            log.info("Successfully created library card {} for user {}", savedCard.getCardNumber(), user.getKeycloakId());
            
            return libraryCardMapper.toResponse(savedCard, user.getPublicId());
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to create library card due to data integrity violation", e);
            throw new EntityValidationException("Failed to create library card: duplicate card number or constraint violation");
        }
    }

    @Override
    public LibraryCardResponse getLibraryCard(UUID cardId) {
        log.info("Retrieving library card: {}", cardId);

        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();

        // Find the library card
        LibraryCardEntity cardEntity = libraryCardRepository.findByPublicIdAndDeletedAtIsNull(cardId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("LibraryCard", cardId));

        // Find the user who owns the card
        UserEntity cardOwner = userRepository.findById(cardEntity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Card owner not found"));

        // Check authorization - users can only access their own cards unless they're admin/librarian
        validateCardAccess(currentUser, cardOwner.getKeycloakId(), "view library card");

        return libraryCardMapper.toResponse(cardEntity, cardOwner.getPublicId());
    }

    @Override
    @Transactional
    public LibraryCardResponse updateLibraryCardStatus(UUID cardId, UpdateCardStatusRequest request) {
        log.info("Updating library card status: {} to {}", cardId, request.getStatus());

        // Validate authentication and authorization
        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();
        validateLibrarianOrAdminAccess(currentUser, "update library card status");

        // Find the library card
        LibraryCardEntity cardEntity = libraryCardRepository.findByPublicIdAndDeletedAtIsNull(cardId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("LibraryCard", cardId));

        // Find the user who owns the card
        UserEntity cardOwner = userRepository.findById(cardEntity.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Card owner not found"));

        // Validate status transition
        validateStatusTransition(cardEntity.getStatus(), request.getStatus());

        // Update the status
        cardEntity.setStatus(request.getStatus());

        try {
            LibraryCardEntity updatedCard = libraryCardRepository.save(cardEntity);
            
            log.info("Successfully updated library card {} status to {} for user {}", 
                    updatedCard.getCardNumber(), request.getStatus(), cardOwner.getKeycloakId());
            
            return libraryCardMapper.toResponse(updatedCard, cardOwner.getPublicId());
        } catch (DataIntegrityViolationException e) {
            log.error("Failed to update library card status due to data integrity violation", e);
            throw new EntityValidationException("Failed to update library card status");
        }
    }

    @Override
    public List<LibraryCardResponse> getUserLibraryCards(String keycloakId) {
        log.info("Retrieving library cards for user: {}", keycloakId);

        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();

        // Check authorization - users can only access their own cards unless they're admin/librarian
        validateCardAccess(currentUser, keycloakId, "view user library cards");

        // Find the user
        UserEntity user = userRepository.findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + keycloakId));

        // Get all library cards for the user
        List<LibraryCardEntity> cardEntities = libraryCardRepository.findByUserKeycloakIdAndDeletedAtIsNull(keycloakId);

        return libraryCardMapper.toResponseList(cardEntities, user.getPublicId());
    }

    @Override
    public List<LibraryCardResponse> getCurrentUserLibraryCards() {
        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();
        return getUserLibraryCards(currentUser.getKeycloakId());
    }

    @Override
    public List<LibraryCardResponse> getUserActiveLibraryCards(String keycloakId) {
        log.info("Retrieving active library cards for user: {}", keycloakId);

        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();

        // Check authorization - users can only access their own cards unless they're admin/librarian
        validateCardAccess(currentUser, keycloakId, "view user active library cards");

        // Find the user
        UserEntity user = userRepository.findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + keycloakId));

        // Get active library cards for the user
        List<LibraryCardEntity> cardEntities = libraryCardRepository.findByUserKeycloakIdAndDeletedAtIsNull(keycloakId)
                .stream()
                .filter(card -> card.getStatus() == LibraryCardStatus.ACTIVE)
                .toList();

        return libraryCardMapper.toResponseList(cardEntities, user.getPublicId());
    }

    @Override
    public boolean hasActiveLibraryCard(String keycloakId) {
        log.info("Checking if user has active library card: {}", keycloakId);

        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();

        // Check authorization - users can only check their own cards unless they're admin/librarian
        validateCardAccess(currentUser, keycloakId, "check user active library cards");

        // Find the user
        UserEntity user = userRepository.findByKeycloakIdAndDeletedAtIsNull(keycloakId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Keycloak ID: " + keycloakId));

        // Check if user has any active cards
        long activeCardCount = libraryCardRepository.countActiveCardsByUserPublicId(user.getPublicId());
        return activeCardCount > 0;
    }

    @Override
    @Transactional
    public void deactivateLibraryCard(UUID cardId) {
        log.info("Deactivating library card: {}", cardId);

        // Validate authentication and authorization
        AuthenticatedUser currentUser = getCurrentAuthenticatedUser();
        validateLibrarianOrAdminAccess(currentUser, "deactivate library card");

        // Find the library card
        LibraryCardEntity cardEntity = libraryCardRepository.findByPublicIdAndDeletedAtIsNull(cardId)
                .orElseThrow(() -> EntityNotFoundException.forPublicId("LibraryCard", cardId));

        // Perform soft delete
        LocalDateTime now = LocalDateTime.now();
        libraryCardRepository.softDeleteByPublicId(cardId, now, now, currentUser.getKeycloakId());

        log.info("Successfully deactivated library card: {}", cardId);
    }

    /**
     * Gets the current authenticated user from the security context.
     *
     * @return the authenticated user
     * @throws AuthenticationException if no user is authenticated
     */
    private AuthenticatedUser getCurrentAuthenticatedUser() {
        AuthenticatedUser user = authenticationService.getCurrentUser();
        if (user == null) {
            throw new AuthenticationException("No authenticated user found");
        }
        return user;
    }

    private void validateLibrarianOrAdminAccess(AuthenticatedUser currentUser, String operation) {
        if (!currentUser.hasRole("ADMIN") && !currentUser.hasRole("LIBRARIAN")) {
            throw AuthorizationException.operationNotAllowed(operation + " - requires ADMIN or LIBRARIAN role");
        }
    }

    /**
     * Validates that the current user can access library cards for the specified user.
     * Users can access their own cards, or ADMIN/LIBRARIAN can access any cards.
     *
     * @param currentUser the current authenticated user
     * @param targetKeycloakId the Keycloak ID of the user whose cards are being accessed
     * @param operation the operation being performed (for error messages)
     * @throws AuthorizationException if user lacks required permissions
     */
    private void validateCardAccess(AuthenticatedUser currentUser, String targetKeycloakId, String operation) {
        boolean isOwnCards = currentUser.getKeycloakId().equals(targetKeycloakId);
        boolean hasAdminAccess = currentUser.hasRole("ADMIN") || currentUser.hasRole("LIBRARIAN");

        if (!isOwnCards && !hasAdminAccess) {
            throw AuthorizationException.operationNotAllowed(operation + " - can only access own cards or requires ADMIN/LIBRARIAN role");
        }
    }

    /**
     * Validates that a status transition is allowed.
     *
     * @param currentStatus the current status of the card
     * @param newStatus the new status to transition to
     * @throws EntityValidationException if the transition is not allowed
     */
    private void validateStatusTransition(LibraryCardStatus currentStatus, LibraryCardStatus newStatus) {
        // Allow any transition for now, but this could be enhanced with business rules
        // For example, you might not allow transitioning from LOST to ACTIVE directly
        if (currentStatus == newStatus) {
            throw new EntityValidationException("Card is already in " + newStatus + " status");
        }

        // Example business rule: Cannot reactivate a lost card directly
        if (currentStatus == LibraryCardStatus.LOST && newStatus == LibraryCardStatus.ACTIVE) {
            throw new EntityValidationException("Cannot reactivate a lost card directly. A new card must be issued.");
        }
    }

    /**
     * Generates a unique card number with retry logic.
     *
     * @return a unique card number
     * @throws EntityValidationException if unable to generate unique number after retries
     */
    private String generateUniqueCardNumber() {
        int maxRetries = 10;
        int attempts = 0;

        while (attempts < maxRetries) {
            String cardNumber = cardNumberGenerator.generateCardNumber();
            
            if (!libraryCardRepository.existsByCardNumberAndDeletedAtIsNull(cardNumber)) {
                return cardNumber;
            }
            
            attempts++;
            log.warn("Generated card number {} already exists, retrying... (attempt {}/{})", 
                    cardNumber, attempts, maxRetries);
        }

        throw new EntityValidationException("Unable to generate unique card number after " + maxRetries + " attempts");
    }
}