package com.library.loan.business.kafka.publisher.impl;

import com.library.loan.business.dto.response.BorrowingResponse;
import com.library.loan.business.kafka.event.BorrowingEvent;
import com.library.loan.business.kafka.event.EventType;
import com.library.loan.business.kafka.publisher.AuditService;
import com.library.loan.business.kafka.publisher.BorrowingAuditService;
import com.library.loan.business.kafka.publisher.BorrowingEventPublisher;
import com.library.loan.repository.entity.Borrowing;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BorrowingAuditServiceImpl implements BorrowingAuditService {

    private final AuditService auditService;
    private final BorrowingEventPublisher borrowingEventPublisher;

    private static final String ENTITY_TYPE = "BORROWING";

    @Override
    public void publishBorrowingCreated(Borrowing borrowing, BorrowingResponse response, String userId) {
        try {
            log.debug("Publishing borrowing created events for borrowing: {}", borrowing.getPublicId());

            // Publish audit event
            auditService.publishCreateEvent(ENTITY_TYPE, borrowing.getPublicId().toString(), response, userId);

            // Publish borrowing lifecycle event
            BorrowingEvent borrowingEvent = BorrowingEvent.create(
                    EventType.CREATED.getValue(),
                    response.getPublicId(),
                    response.getBookCopyPublicId(),
                    response.getUserPublicId(),
                    response.getBorrowDate(),
                    response.getDueDate(),
                    response.getReturnDate(),
                    response.getStatus().toString(),
                    response.getFineAmount(),
                    userId,
                    MDC.get("correlationId")
            );

            borrowingEventPublisher.publishEvent(borrowingEvent);

            log.debug("Successfully published borrowing created events for borrowing: {}", borrowing.getPublicId());

        } catch (Exception e) {
            log.error("Failed to publish borrowing created events for borrowing {}: {}", 
                     borrowing.getPublicId(), e.getMessage(), e);
        }
    }

    @Override
    public void publishBookReturned(Borrowing oldBorrowing, Borrowing updatedBorrowing, 
                                  BorrowingResponse response, String userId) {
        try {
            log.debug("Publishing book returned events for borrowing: {}", updatedBorrowing.getPublicId());

            // Create old borrowing representation for audit
            BorrowingResponse oldResponse = createBorrowingResponse(oldBorrowing);

            // Publish audit event
            auditService.publishEvent(EventType.RETURNED, ENTITY_TYPE, 
                                    updatedBorrowing.getPublicId().toString(), 
                                    oldResponse, response, userId);

            // Publish borrowing lifecycle event
            BorrowingEvent borrowingEvent = BorrowingEvent.create(
                    EventType.RETURNED.getValue(),
                    response.getPublicId(),
                    response.getBookCopyPublicId(),
                    response.getUserPublicId(),
                    response.getBorrowDate(),
                    response.getDueDate(),
                    response.getReturnDate(),
                    response.getStatus().toString(),
                    response.getFineAmount(),
                    userId,
                    MDC.get("correlationId")
            );

            borrowingEventPublisher.publishEvent(borrowingEvent);

            log.debug("Successfully published book returned events for borrowing: {}", updatedBorrowing.getPublicId());

        } catch (Exception e) {
            log.error("Failed to publish book returned events for borrowing {}: {}", 
                     updatedBorrowing.getPublicId(), e.getMessage(), e);
        }
    }

    @Override
    public void publishBorrowingRenewed(Borrowing oldBorrowing, Borrowing updatedBorrowing, 
                                      BorrowingResponse response, String userId) {
        try {
            log.debug("Publishing borrowing renewed events for borrowing: {}", updatedBorrowing.getPublicId());

            // Create old borrowing representation for audit
            BorrowingResponse oldResponse = createBorrowingResponse(oldBorrowing);

            // Publish audit event
            auditService.publishEvent(EventType.RENEWED, ENTITY_TYPE, 
                                    updatedBorrowing.getPublicId().toString(), 
                                    oldResponse, response, userId);

            // Publish borrowing lifecycle event
            BorrowingEvent borrowingEvent = BorrowingEvent.create(
                    EventType.RENEWED.getValue(),
                    response.getPublicId(),
                    response.getBookCopyPublicId(),
                    response.getUserPublicId(),
                    response.getBorrowDate(),
                    response.getDueDate(),
                    response.getReturnDate(),
                    response.getStatus().toString(),
                    response.getFineAmount(),
                    userId,
                    MDC.get("correlationId")
            );

            borrowingEventPublisher.publishEvent(borrowingEvent);

            log.debug("Successfully published borrowing renewed events for borrowing: {}", updatedBorrowing.getPublicId());

        } catch (Exception e) {
            log.error("Failed to publish borrowing renewed events for borrowing {}: {}", 
                     updatedBorrowing.getPublicId(), e.getMessage(), e);
        }
    }

    @Override
    public void publishBorrowingDeleted(Borrowing borrowing, String userId) {
        try {
            log.debug("Publishing borrowing deleted events for borrowing: {}", borrowing.getPublicId());

            // Create borrowing representation for audit
            BorrowingResponse borrowingResponse = createBorrowingResponse(borrowing);

            // Publish audit event
            auditService.publishDeleteEvent(ENTITY_TYPE, borrowing.getPublicId().toString(), 
                                          borrowingResponse, userId);

            // Publish borrowing lifecycle event
            BorrowingEvent borrowingEvent = BorrowingEvent.create(
                    EventType.DELETED.getValue(),
                    borrowing.getPublicId(),
                    UUID.randomUUID(), // Placeholder - resolve from catalog-service
                    UUID.randomUUID(), // Placeholder - resolve from member-service
                    borrowing.getBorrowDate(),
                    borrowing.getDueDate(),
                    borrowing.getReturnDate(),
                    borrowing.getStatus().toString(),
                    null, // No fine amount for deletion
                    userId,
                    MDC.get("correlationId")
            );

            borrowingEventPublisher.publishEvent(borrowingEvent);

            log.debug("Successfully published borrowing deleted events for borrowing: {}", borrowing.getPublicId());

        } catch (Exception e) {
            log.error("Failed to publish borrowing deleted events for borrowing {}: {}", 
                     borrowing.getPublicId(), e.getMessage(), e);
        }
    }

    @Override
    public void publishBorrowingAccessed(Borrowing borrowing, String userId, String accessType) {
        try {
            log.debug("Publishing borrowing accessed event for borrowing: {}, access type: {}", 
                     borrowing.getPublicId(), accessType);

            String details = String.format("Borrowing accessed via %s operation", accessType);
            auditService.publishAccessEvent(ENTITY_TYPE, borrowing.getPublicId().toString(), userId, details);

            log.debug("Successfully published borrowing accessed event for borrowing: {}", borrowing.getPublicId());

        } catch (Exception e) {
            log.error("Failed to publish borrowing accessed event for borrowing {}: {}", 
                     borrowing.getPublicId(), e.getMessage(), e);
        }
    }

    /**
     * Creates a BorrowingResponse from a Borrowing entity for audit purposes.
     * This is a simplified version without external service resolution.
     */
    private BorrowingResponse createBorrowingResponse(Borrowing borrowing) {
        BorrowingResponse response = new BorrowingResponse();
        response.setPublicId(borrowing.getPublicId());
        response.setBorrowDate(borrowing.getBorrowDate());
        response.setDueDate(borrowing.getDueDate());
        response.setReturnDate(borrowing.getReturnDate());
        response.setStatus(borrowing.getStatus());
        response.setCreatedAt(borrowing.getCreatedAt());
        response.setUpdatedAt(borrowing.getUpdatedAt());
        
        // Note: In production, resolve these from external services
        response.setBookCopyPublicId(UUID.randomUUID());
        response.setUserPublicId(UUID.randomUUID());
        
        return response;
    }
}