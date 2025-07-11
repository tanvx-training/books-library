package com.library.user.application.service;

import com.library.user.application.dto.response.LibraryCardResponse;
import com.library.user.application.exception.UserApplicationException;
import com.library.user.application.mapper.UserDtoMapper;
import com.library.user.domain.exception.InvalidUserDataException;
import com.library.user.domain.exception.UserNotFoundException;
import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.user.UserId;
import com.library.user.domain.repository.LibraryCardRepository;
import com.library.user.domain.repository.UserRepository;
import com.library.user.domain.service.UserDomainService;
import com.library.user.infrastructure.enums.LogLevel;
import com.library.user.infrastructure.enums.OperationType;
import com.library.user.infrastructure.event.DomainEventPublisher;
import com.library.user.infrastructure.logging.Loggable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LibraryCardApplicationService {

    private final UserDomainService userDomainService;
    private final LibraryCardRepository libraryCardRepository;
    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final DomainEventPublisher eventPublisher;

    /**
     * Get all library cards for a user
     */
    @Transactional(readOnly = true)
    @Loggable(
            level = LogLevel.DETAILED,
            operationType = OperationType.READ,
            resourceType = "LibraryCard",
            logReturnValue = false,
            performanceThresholdMs = 500L,
            messagePrefix = "LIBRARY_CARD_APP_SERVICE_LIST",
            customTags = {
                    "layer=application",
                    "transaction=readonly"
            }
    )
    public List<LibraryCardResponse> getLibraryCardsForUser(Long userId) {
        try {
            // Verify user exists
            if (!userRepository.findById(UserId.of(userId)).isPresent()) {
                throw new UserNotFoundException(userId);
            }

            List<LibraryCard> libraryCards = libraryCardRepository.findByUserId(UserId.of(userId));

            return libraryCards.stream()
                    .map(userDtoMapper::toLibraryCardResponse)
                    .collect(Collectors.toList());
        } catch (UserNotFoundException e) {
            throw new UserApplicationException("User not found with ID: " + userId, e);
        } catch (Exception e) {
            throw new UserApplicationException("Failed to get library cards for user: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new library card for a user
     */
    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.CREATE,
            resourceType = "LibraryCard",
            includeInPerformanceMonitoring = true,
            performanceThresholdMs = 1500L,
            messagePrefix = "LIBRARY_CARD_APP_SERVICE_CREATE",
            customTags = {
                    "layer=application",
                    "transaction=write",
                    "card_management=true"
            }
    )
    public LibraryCardResponse createLibraryCard(Long userId) {
        try {
            LibraryCard libraryCard = userDomainService.createLibraryCardForUser(userId);

            // Publish domain events
            eventPublisher.publishAll(libraryCard.getDomainEvents());
            libraryCard.clearEvents();

            return userDtoMapper.toLibraryCardResponse(libraryCard);
        } catch (UserNotFoundException e) {
            throw new UserApplicationException("User not found with ID: " + userId, e);
        } catch (InvalidUserDataException e) {
            throw new UserApplicationException("Invalid data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new UserApplicationException("Failed to create library card: " + e.getMessage(), e);
        }
    }

    /**
     * Renew a library card
     */
    @Transactional
    @Loggable(
            level = LogLevel.ADVANCED,
            operationType = OperationType.UPDATE,
            resourceType = "LibraryCard",
            includeInPerformanceMonitoring = true,
            performanceThresholdMs = 1500L,
            messagePrefix = "LIBRARY_CARD_APP_SERVICE_RENEW",
            customTags = {
                    "layer=application",
                    "transaction=write",
                    "card_management=true"
            }
    )
    public LibraryCardResponse renewLibraryCard(Long cardId) {
        try {
            LibraryCard libraryCard = userDomainService.renewLibraryCard(cardId);

            // Publish domain events
            eventPublisher.publishAll(libraryCard.getDomainEvents());
            libraryCard.clearEvents();

            return userDtoMapper.toLibraryCardResponse(libraryCard);
        } catch (InvalidUserDataException e) {
            throw new UserApplicationException("Invalid data: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new UserApplicationException("Failed to renew library card: " + e.getMessage(), e);
        }
    }
}