package com.library.user.infrastructure.event;

import com.library.user.domain.event.CardCreatedEvent;
import com.library.user.domain.event.KafkaEvent;
import com.library.user.domain.event.LibraryCardCreatedEvent;
import com.library.user.domain.event.LibraryCardRenewedEvent;
import com.library.user.domain.event.UserCreatedEvent;
import com.library.user.domain.event.UserUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventHandler {
    // User-related events
    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String CARD_CREATED = "CARD_CREATED";
    public static final String CARD_RENEWED = "CARD_RENEWED";
    public static final String CARD_EXPIRED = "CARD_EXPIRED";
    public static final String CARD_EXPIRING_SOON = "CARD_EXPIRING_SOON";
    public static final String PASSWORD_RESET = "PASSWORD_RESET";
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String USER_TOPIC = "user-events";
    private static final String CARD_TOPIC = "card-events";
    private static final String SOURCE = "user-service";

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        log.info("Handling UserCreatedEvent for user ID: {}", event.getId());

        // Convert domain event to common event format
        UserCreatedEvent commonEvent = UserCreatedEvent.builder()
                .id(event.getId())
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .username(event.getUsername())
                .build();

        // Create Kafka event
        KafkaEvent<UserCreatedEvent> kafkaEvent =
                KafkaEvent.create(USER_CREATED, SOURCE, commonEvent);

        // Send to Kafka
        kafkaTemplate.send(USER_TOPIC, kafkaEvent);
        log.debug("UserCreatedEvent published to Kafka for user ID: {}", event.getId());
    }

    @EventListener
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        log.info("Handling UserUpdatedEvent for user ID: {}", event.getId());

        // Convert domain event to common event format
        UserUpdatedEvent commonEvent = UserUpdatedEvent.builder()
                .id(event.getId())
                .email(event.getEmail())
                .firstName(event.getFirstName())
                .lastName(event.getLastName())
                .username(event.getUsername())
                .build();

        // Create Kafka event
        KafkaEvent<UserUpdatedEvent> kafkaEvent =
                KafkaEvent.create(USER_UPDATED, SOURCE, commonEvent);

        // Send to Kafka
        kafkaTemplate.send(USER_TOPIC, kafkaEvent);
        log.debug("UserUpdatedEvent published to Kafka for user ID: {}", event.getId());
    }

    @EventListener
    public void handleLibraryCardCreatedEvent(LibraryCardCreatedEvent event) {
        log.info("Handling LibraryCardCreatedEvent for card ID: {}", event.getId());

        // Convert domain event to common event format
        CardCreatedEvent commonEvent = CardCreatedEvent.builder()
                .userId(event.getUserId())
                .cardNumber(event.getCardNumber())
                .issueDate(event.getIssueDate())
                .expiryDate(event.getExpiryDate())
                .cardType("STANDARD") // Default or determine based on business rules
                .build();

        // Create Kafka event
        KafkaEvent<CardCreatedEvent> kafkaEvent =
                KafkaEvent.create(CARD_CREATED, SOURCE, commonEvent);

        // Send to Kafka
        kafkaTemplate.send(CARD_TOPIC, kafkaEvent);
        log.debug("LibraryCardCreatedEvent published to Kafka for card ID: {}", event.getId());
    }

    @EventListener
    public void handleLibraryCardRenewedEvent(LibraryCardRenewedEvent event) {
        log.info("Handling LibraryCardRenewedEvent for card ID: {}", event.getId());

        // Here you would create a specific event for card renewal
        // For now, we'll just log it since there's no direct equivalent in common events
        log.info("Library card renewed: ID={}, userId={}, newExpiryDate={}",
                event.getId(), event.getUserId(), event.getNewExpiryDate());
    }
}