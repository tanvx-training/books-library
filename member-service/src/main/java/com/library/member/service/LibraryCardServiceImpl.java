package com.library.member.service;

import com.library.member.dto.request.CreateLibraryCardRequest;
import com.library.member.dto.request.UpdateCardStatusRequest;
import com.library.member.dto.response.LibraryCardResponse;
import com.library.member.aop.AuthenticationException;
import com.library.member.aop.AuthorizationException;
import com.library.member.aop.EntityNotFoundException;
import com.library.member.aop.EntityValidationException;

import com.library.member.framework.kafka.AuditService;
import com.library.member.dto.model.AuthenticatedUser;
import com.library.member.framework.CardNumberGenerator;
import com.library.member.repository.LibraryCardRepository;
import com.library.member.repository.UserRepository;
import com.library.member.repository.LibraryCardEntity;
import com.library.member.repository.UserEntity;
import com.library.member.repository.LibraryCardStatus;
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
    private final UnifiedAuthenticationService unifiedAuthenticationService;
    private final AuditService auditService;

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
            auditService.publishCreateEvent(
                    "LibraryCard",
                    savedCard.getPublicId().toString(),
                    savedCard,
                    unifiedAuthenticationService.getCurrentUserKeycloakId()
                    );
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
        LibraryCardEntity oldCard = new LibraryCardEntity();
        oldCard.setId(cardEntity.getId());
        oldCard.setPublicId(cardEntity.getPublicId());
        oldCard.setCardNumber(cardEntity.getCardNumber());
        oldCard.setUserId(cardEntity.getUserId());
        oldCard.setIssueDate(cardEntity.getIssueDate());
        oldCard.setExpiryDate(cardEntity.getExpiryDate());
        oldCard.setStatus(request.getStatus());

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
            auditService.publishUpdateEvent(
                    "LibraryCard",
                    updatedCard.getPublicId().toString(),
                    oldCard,
                    updatedCard,
                    unifiedAuthenticationService.getCurrentUserKeycloakId()
            );
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

    private AuthenticatedUser getCurrentAuthenticatedUser() {
        AuthenticatedUser user = unifiedAuthenticationService.getCurrentUser();
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

    private void validateCardAccess(AuthenticatedUser currentUser, String targetKeycloakId, String operation) {
        boolean isOwnCards = currentUser.getKeycloakId().equals(targetKeycloakId);
        boolean hasAdminAccess = currentUser.hasRole("ADMIN") || currentUser.hasRole("LIBRARIAN");

        if (!isOwnCards && !hasAdminAccess) {
            throw AuthorizationException.operationNotAllowed(operation + " - can only access own cards or requires ADMIN/LIBRARIAN role");
        }
    }

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