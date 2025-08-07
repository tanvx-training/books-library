package com.library.notification.business.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.notification.business.NotificationDeliveryService;
import com.library.notification.business.NotificationPreferencesService;
import com.library.notification.business.dto.event.*;
import com.library.notification.business.dto.request.CreateNotificationRequest;
import com.library.notification.business.mapper.NotificationMapper;
import com.library.notification.repository.NotificationRepository;
import com.library.notification.repository.entity.Notification;
import com.library.notification.repository.enums.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Kafka consumer for processing library events and creating notifications
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class NotificationEventConsumer {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferencesService preferencesService;
    private final NotificationDeliveryService deliveryService;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "book-borrowed", groupId = "notification-service")
    public void handleBookBorrowedEvent(
            @Payload String eventPayload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            Acknowledgment acknowledgment) {
        
        log.info("Received book borrowed event from topic: {}, partition: {}", topic, partition);
        
        try {
            BookBorrowedEvent event = objectMapper.readValue(eventPayload, BookBorrowedEvent.class);
            processBookBorrowedEvent(event);
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Failed to process book borrowed event: {}", eventPayload, e);
            // In a real implementation, you might want to send to a dead letter queue
            acknowledgment.acknowledge(); // Acknowledge to prevent infinite retries
        }
    }

    @KafkaListener(topics = "book-returned", groupId = "notification-service")
    public void handleBookReturnedEvent(
            @Payload String eventPayload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        
        log.info("Received book returned event from topic: {}", topic);
        
        try {
            BookReturnedEvent event = objectMapper.readValue(eventPayload, BookReturnedEvent.class);
            processBookReturnedEvent(event);
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Failed to process book returned event: {}", eventPayload, e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "book-overdue", groupId = "notification-service")
    public void handleBookOverdueEvent(
            @Payload String eventPayload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        
        log.info("Received book overdue event from topic: {}", topic);
        
        try {
            BookOverdueEvent event = objectMapper.readValue(eventPayload, BookOverdueEvent.class);
            processBookOverdueEvent(event);
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Failed to process book overdue event: {}", eventPayload, e);
            acknowledgment.acknowledge();
        }
    }

    @KafkaListener(topics = "book-reservation-available", groupId = "notification-service")
    public void handleBookReservationAvailableEvent(
            @Payload String eventPayload,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment acknowledgment) {
        
        log.info("Received book reservation available event from topic: {}", topic);
        
        try {
            BookReservationAvailableEvent event = objectMapper.readValue(eventPayload, BookReservationAvailableEvent.class);
            processBookReservationAvailableEvent(event);
            acknowledgment.acknowledge();
            
        } catch (Exception e) {
            log.error("Failed to process book reservation available event: {}", eventPayload, e);
            acknowledgment.acknowledge();
        }
    }

    private void processBookBorrowedEvent(BookBorrowedEvent event) {
        log.info("Processing book borrowed event for user: {} and book: {}", event.getUserPublicId(), event.getBookTitle());
        
        // Check if user wants borrow notifications
        if (!preferencesService.isNotificationTypeEnabled(event.getUserPublicId(), "borrow_notification")) {
            log.info("Borrow notifications disabled for user: {}", event.getUserPublicId());
            return;
        }
        
        // Create notifications for enabled channels
        createNotificationsForEvent(
            event.getUserPublicId(),
            "Book Borrowed Successfully",
            String.format("You have successfully borrowed '%s'. Due date: %s", 
                event.getBookTitle(), event.getDueDate())
        );
    }

    private void processBookReturnedEvent(BookReturnedEvent event) {
        log.info("Processing book returned event for user: {} and book: {}", event.getUserPublicId(), event.getBookTitle());
        
        // Check if user wants return notifications
        if (!preferencesService.isNotificationTypeEnabled(event.getUserPublicId(), "return_reminder")) {
            log.info("Return notifications disabled for user: {}", event.getUserPublicId());
            return;
        }
        
        String content = event.isWasOverdue() 
            ? String.format("Thank you for returning '%s'. Please note this book was returned late.", event.getBookTitle())
            : String.format("Thank you for returning '%s' on time.", event.getBookTitle());
        
        createNotificationsForEvent(
            event.getUserPublicId(),
            "Book Returned",
            content
        );
    }

    private void processBookOverdueEvent(BookOverdueEvent event) {
        log.info("Processing book overdue event for user: {} and book: {}", event.getUserPublicId(), event.getBookTitle());
        
        // Check if user wants overdue notifications
        if (!preferencesService.isNotificationTypeEnabled(event.getUserPublicId(), "overdue_notification")) {
            log.info("Overdue notifications disabled for user: {}", event.getUserPublicId());
            return;
        }
        
        createNotificationsForEvent(
            event.getUserPublicId(),
            "Overdue Book Reminder",
            String.format("Your book '%s' is %d day(s) overdue. Please return it as soon as possible to avoid additional fees.", 
                event.getBookTitle(), event.getDaysOverdue())
        );
    }

    private void processBookReservationAvailableEvent(BookReservationAvailableEvent event) {
        log.info("Processing book reservation available event for user: {} and book: {}", 
            event.getUserPublicId(), event.getBookTitle());
        
        // Check if user wants reservation notifications
        if (!preferencesService.isNotificationTypeEnabled(event.getUserPublicId(), "reservation_notification")) {
            log.info("Reservation notifications disabled for user: {}", event.getUserPublicId());
            return;
        }
        
        createNotificationsForEvent(
            event.getUserPublicId(),
            "Reserved Book Available",
            String.format("Your reserved book '%s' is now available for pickup. Please collect it by %s.", 
                event.getBookTitle(), event.getAvailableUntil())
        );
    }

    private void createNotificationsForEvent(UUID userPublicId, String title, String content) {
        // Create notifications for each enabled channel
        for (NotificationType type : NotificationType.values()) {
            if (preferencesService.isChannelEnabled(userPublicId, type.name())) {
                createAndDeliverNotification(userPublicId, title, content, type);
            }
        }
    }

    private void createAndDeliverNotification(UUID userPublicId, String title, String content, NotificationType type) {
        try {
            CreateNotificationRequest request = new CreateNotificationRequest();
            request.setUserPublicId(userPublicId);
            request.setTitle(title);
            request.setContent(content);
            request.setType(type);
            
            Notification entity = notificationMapper.toEntity(request);
            Notification savedEntity = notificationRepository.save(entity);
            
            log.info("Created notification: {} for user: {} with type: {}", 
                savedEntity.getPublicId(), userPublicId, type);
            
            // Attempt to deliver the notification asynchronously
            try {
                deliveryService.deliverNotification(savedEntity);
            } catch (Exception e) {
                log.error("Failed to deliver notification: {}", savedEntity.getPublicId(), e);
                // Notification is saved, delivery can be retried later
            }
            
        } catch (Exception e) {
            log.error("Failed to create notification for user: {} with type: {}", userPublicId, type, e);
        }
    }
}