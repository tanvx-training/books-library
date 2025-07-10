package com.library.user.service.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.constants.EventType;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.common.event.CardCreatedEvent;
import com.library.common.event.CardExpiredEvent;
import com.library.common.event.CardExpiringSoonEvent;
import com.library.common.event.CardRenewedEvent;
import com.library.common.aop.exception.BadRequestException;
import com.library.common.aop.exception.ResourceNotFoundException;
import com.library.user.infrastructure.enums.LibraryCardStatus;
import com.library.user.infrastructure.persistence.entity.LibraryCard;
import com.library.user.infrastructure.persistence.entity.User;
import com.library.user.service.KafkaProducerService;
import com.library.user.service.LibraryCardService;
import com.library.user.repository.LibraryCardRepository;
import com.library.user.repository.UserRepository;
import com.library.user.dto.request.CreateLibraryCardRequestDTO;
import com.library.user.dto.request.RenewLibraryCardRequestDTO;
import com.library.user.dto.request.UpdateLibraryCardStatusRequestDTO;
import com.library.user.dto.response.LibraryCardResponseDTO;
import com.library.user.utils.mapper.LibraryCardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryCardServiceImpl implements LibraryCardService {

    private final UserRepository userRepository;
    private final LibraryCardRepository libraryCardRepository;
    private final LibraryCardMapper libraryCardMapper;
    private final KafkaProducerService kafkaProducerService;
    
    @Value("${spring.kafka.topics.library-events:library-events}")
    private String libraryEventsTopic;
    
    @Value("${app.library-card.default-validity-years:1}")
    private int defaultValidityYears;
    
    @Value("${app.library-card.expiring-soon-days:30}")
    private int expiringSoonDays;

    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.CREATE,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 3000L,
        messagePrefix = "LIBRARY_CARD_SERVICE_CREATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_critical=true",
            "uniqueness_check=true",
            "card_generation=true",
            "event_publishing=true",
            "user_validation=true"
        }
    )
    public LibraryCardResponseDTO createLibraryCard(CreateLibraryCardRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", requestDTO.getUserId()));
        
        // Check if user already has an active library card
        libraryCardRepository.findByUserIdAndStatus(user.getId(), LibraryCardStatus.ACTIVE.name())
                .ifPresent(card -> {
                    throw new BadRequestException("User already has an active library card with number: " + card.getCardNumber());
                });
        
        // Create new library card
        LibraryCard libraryCard = new LibraryCard();
        libraryCard.setCardNumber(generateUniqueCardNumber());
        libraryCard.setIssueDate(LocalDate.now());
        
        // Use provided expiry date or default (1 year from now)
        LocalDate expiryDate = requestDTO.getExpiryDate() != null ? 
                requestDTO.getExpiryDate() : 
                LocalDate.now().plusYears(defaultValidityYears);
        
        libraryCard.setExpiryDate(expiryDate);
        libraryCard.setStatus(LibraryCardStatus.ACTIVE.name());
        libraryCard.setUser(user);
        
        LibraryCard savedCard = libraryCardRepository.save(libraryCard);
        log.info("Created new library card with number {} for user {}", savedCard.getCardNumber(), user.getId());
        
        // Send Kafka event
        sendCardCreatedEvent(savedCard);
        
        return libraryCardMapper.toLibraryCardResponseDTO(savedCard);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "LIBRARY_CARD_SERVICE_DETAIL",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "single_entity=true",
            "card_lookup=true"
        }
    )
    public LibraryCardResponseDTO getLibraryCardById(Long id) {
        LibraryCard libraryCard = findLibraryCardById(id);
        return libraryCardMapper.toLibraryCardResponseDTO(libraryCard);
    }
    
    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = false, // Don't log card collections - can contain sensitive info
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "LIBRARY_CARD_SERVICE_BY_USER",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "relationship_query=true",
            "user_cards_lookup=true",
            "user_validation=true"
        }
    )
    public List<LibraryCardResponseDTO> getLibraryCardsByUserId(Long userId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        List<LibraryCard> libraryCards = libraryCardRepository.findByUserId(userId);
        return libraryCards.stream()
                .map(libraryCardMapper::toLibraryCardResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.BASIC,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = false, // Don't log all cards - can be large and sensitive
        logExecutionTime = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "LIBRARY_CARD_SERVICE_LIST",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "status_filter=true",
            "admin_operation=true"
        }
    )
    public List<LibraryCardResponseDTO> getAllLibraryCards(LibraryCardStatus status) {
        List<LibraryCard> libraryCards;
        
        if (status != null) {
            libraryCards = libraryCardRepository.findByStatus(status.name());
        } else {
            libraryCards = libraryCardRepository.findAll();
        }
        
        return libraryCards.stream()
                .map(libraryCardMapper::toLibraryCardResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "LIBRARY_CARD_SERVICE_STATUS_UPDATE",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_critical=true",
            "status_change=true",
            "admin_operation=true",
            "audit_required=true"
        }
    )
    public LibraryCardResponseDTO updateLibraryCardStatus(Long id, UpdateLibraryCardStatusRequestDTO requestDTO) {
        LibraryCard libraryCard = findLibraryCardById(id);
        
        // Update status
        libraryCard.setStatus(requestDTO.getStatus().name());
        LibraryCard updatedCard = libraryCardRepository.save(libraryCard);
        
        log.info("Updated library card {} status to {}", id, requestDTO.getStatus());
        
        // TODO: Send Kafka event for status change if needed
        
        return libraryCardMapper.toLibraryCardResponseDTO(updatedCard);
    }
    
    @Override
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 3000L,
        messagePrefix = "LIBRARY_CARD_SERVICE_RENEWAL",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "business_critical=true",
            "renewal_operation=true",
            "business_validation=true",
            "event_publishing=true",
            "expiry_update=true",
            "audit_required=true"
        }
    )
    public LibraryCardResponseDTO renewLibraryCard(Long id, RenewLibraryCardRequestDTO requestDTO) {
        LibraryCard libraryCard = findLibraryCardById(id);
        
        // Check if card is in a state that can be renewed (not LOST or BLOCKED)
        if (LibraryCardStatus.LOST.name().equals(libraryCard.getStatus()) || 
            LibraryCardStatus.BLOCKED.name().equals(libraryCard.getStatus())) {
            throw new BadRequestException("Cannot renew library card with status: " + libraryCard.getStatus());
        }
        
        // Store previous expiry date for event
        LocalDate previousExpiryDate = libraryCard.getExpiryDate();
        
        // Update expiry date
        libraryCard.setExpiryDate(requestDTO.getNewExpiryDate());
        
        // If card was expired, set it back to active
        if (LibraryCardStatus.EXPIRED.name().equals(libraryCard.getStatus())) {
            libraryCard.setStatus(LibraryCardStatus.ACTIVE.name());
        }
        
        LibraryCard renewedCard = libraryCardRepository.save(libraryCard);
        log.info("Renewed library card {} with new expiry date {}", id, requestDTO.getNewExpiryDate());
        
        // Send Kafka event
        sendCardRenewedEvent(renewedCard, previousExpiryDate);
        
        return libraryCardMapper.toLibraryCardResponseDTO(renewedCard);
    }
    
    /**
     * Scheduled task to check for expired cards and update their status
     * Runs at midnight every day
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "LibraryCard",
        logArguments = false, // No arguments for scheduled job
        logReturnValue = false, // Void method
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 10000L, // Batch job can take longer
        messagePrefix = "LIBRARY_CARD_SCHEDULED_EXPIRY_CHECK",
        customTags = {
            "layer=service", 
            "transaction=write", 
            "scheduled_job=true",
            "business_critical=true",
            "batch_operation=true",
            "status_update=true",
            "event_publishing=true",
            "expiry_management=true"
        }
    )
    public void checkExpiredCards() {
        LocalDate today = LocalDate.now();
        List<LibraryCard> expiredCards = libraryCardRepository.findByExpiryDateBeforeAndStatus(today, LibraryCardStatus.ACTIVE.name());
        
        log.info("Found {} expired library cards", expiredCards.size());
        
        for (LibraryCard card : expiredCards) {
            // Update status to EXPIRED
            card.setStatus(LibraryCardStatus.EXPIRED.name());
            libraryCardRepository.save(card);
            
            // Send expired event
            sendCardExpiredEvent(card);
            
            log.info("Updated library card {} status to EXPIRED", card.getId());
        }
    }
    
    /**
     * Scheduled task to check for cards that are expiring soon and send notifications
     * Runs at 1 AM every day
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional(readOnly = true)
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = false, // No arguments for scheduled job
        logReturnValue = false, // Void method
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 8000L, // Batch notification job
        messagePrefix = "LIBRARY_CARD_SCHEDULED_EXPIRING_CHECK",
        customTags = {
            "layer=service", 
            "transaction=readonly", 
            "scheduled_job=true",
            "notification_operation=true",
            "batch_operation=true",
            "event_publishing=true",
            "expiry_notification=true",
            "business_critical=true"
        }
    )
    public void checkExpiringCards() {
        LocalDate today = LocalDate.now();
        LocalDate expiryDateStart = today;
        LocalDate expiryDateEnd = today.plusDays(expiringSoonDays);
        
        List<LibraryCard> expiringCards = libraryCardRepository.findCardsByExpiryDateBetweenAndStatus(
                expiryDateStart, expiryDateEnd, LibraryCardStatus.ACTIVE.name());
        
        log.info("Found {} library cards expiring in the next {} days", expiringCards.size(), expiringSoonDays);
        
        for (LibraryCard card : expiringCards) {
            // Calculate days until expiry
            int daysUntilExpiry = (int) java.time.temporal.ChronoUnit.DAYS.between(today, card.getExpiryDate());
            
            // Send expiring soon event
            sendCardExpiringSoonEvent(card, daysUntilExpiry);
            
            log.info("Sent expiring soon notification for library card {} (expires in {} days)", 
                    card.getId(), daysUntilExpiry);
        }
    }
    
    /**
     * Helper method to find a library card by ID
     * @param id the library card ID
     * @return the library card
     * @throws ResourceNotFoundException if the library card is not found
     */
    private LibraryCard findLibraryCardById(Long id) {
        return libraryCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Library Card", "id", id));
    }
    
    /**
     * Generate a unique card number
     * @return unique card number
     */
    private String generateUniqueCardNumber() {
        return "LC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Send card created event to Kafka
     * @param libraryCard the created library card
     */
    private void sendCardCreatedEvent(LibraryCard libraryCard) {
        User user = libraryCard.getUser();
        
        CardCreatedEvent event = CardCreatedEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .cardNumber(libraryCard.getCardNumber())
                .issueDate(libraryCard.getIssueDate())
                .expiryDate(libraryCard.getExpiryDate())
                .cardType("STANDARD") // Could be parameterized in the future
                .build();
        
        kafkaProducerService.createAndSendEvent(
                libraryEventsTopic,
                user.getId().toString(),
                EventType.CARD_CREATED,
                "user-service",
                event
        );
        
        log.info("Sent CARD_CREATED event for library card {}", libraryCard.getId());
    }
    
    /**
     * Send card renewed event to Kafka
     * @param libraryCard the renewed library card
     * @param previousExpiryDate the previous expiry date
     */
    private void sendCardRenewedEvent(LibraryCard libraryCard, LocalDate previousExpiryDate) {
        User user = libraryCard.getUser();
        
        CardRenewedEvent event = CardRenewedEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .cardNumber(libraryCard.getCardNumber())
                .previousExpiryDate(previousExpiryDate)
                .newExpiryDate(libraryCard.getExpiryDate())
                .cardType("STANDARD") // Could be parameterized in the future
                .build();
        
        kafkaProducerService.createAndSendEvent(
                libraryEventsTopic,
                user.getId().toString(),
                EventType.CARD_RENEWED,
                "user-service",
                event
        );
        
        log.info("Sent CARD_RENEWED event for library card {}", libraryCard.getId());
    }
    
    /**
     * Send card expired event to Kafka
     * @param libraryCard the expired library card
     */
    private void sendCardExpiredEvent(LibraryCard libraryCard) {
        User user = libraryCard.getUser();
        
        CardExpiredEvent event = CardExpiredEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .cardNumber(libraryCard.getCardNumber())
                .expiryDate(libraryCard.getExpiryDate())
                .cardType("STANDARD") // Could be parameterized in the future
                .renewalInstructions("Vui lòng liên hệ thủ thư để gia hạn thẻ của bạn.")
                .build();
        
        kafkaProducerService.createAndSendEvent(
                libraryEventsTopic,
                user.getId().toString(),
                EventType.CARD_EXPIRED,
                "user-service",
                event
        );
        
        log.info("Sent CARD_EXPIRED event for library card {}", libraryCard.getId());
    }
    
    /**
     * Send card expiring soon event to Kafka
     * @param libraryCard the expiring library card
     * @param daysUntilExpiry number of days until expiry
     */
    private void sendCardExpiringSoonEvent(LibraryCard libraryCard, int daysUntilExpiry) {
        User user = libraryCard.getUser();
        
        CardExpiringSoonEvent event = CardExpiringSoonEvent.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .cardNumber(libraryCard.getCardNumber())
                .expiryDate(libraryCard.getExpiryDate())
                .cardType("STANDARD") // Could be parameterized in the future
                .daysUntilExpiry(daysUntilExpiry)
                .renewalInstructions("Bạn có thể gia hạn thẻ trực tuyến hoặc liên hệ thủ thư.")
                .build();
        
        kafkaProducerService.createAndSendEvent(
                libraryEventsTopic,
                user.getId().toString(),
                EventType.CARD_EXPIRING_SOON,
                "user-service",
                event
        );
    }
}
