package com.library.loan.framework.kafka;

import com.library.loan.dto.response.BorrowingResponse;
import com.library.loan.repository.Borrowing;

/**
 * Service interface for borrowing-specific audit logging and event publishing.
 * Provides specialized methods for borrowing lifecycle events.
 */
public interface BorrowingAuditService {

    /**
     * Publishes audit and lifecycle events for borrowing creation.
     * @param borrowing the created borrowing entity
     * @param response the borrowing response DTO
     * @param userId the ID of the user who created the borrowing
     */
    void publishBorrowingCreated(Borrowing borrowing, BorrowingResponse response, String userId);

    /**
     * Publishes audit and lifecycle events for book return.
     * @param oldBorrowing the borrowing entity before return
     * @param updatedBorrowing the borrowing entity after return
     * @param response the borrowing response DTO
     * @param userId the ID of the user who processed the return
     */
    void publishBookReturned(Borrowing oldBorrowing, Borrowing updatedBorrowing, 
                           BorrowingResponse response, String userId);

    /**
     * Publishes audit and lifecycle events for borrowing renewal.
     * @param oldBorrowing the borrowing entity before renewal
     * @param updatedBorrowing the borrowing entity after renewal
     * @param response the borrowing response DTO
     * @param userId the ID of the user who renewed the borrowing
     */
    void publishBorrowingRenewed(Borrowing oldBorrowing, Borrowing updatedBorrowing, 
                               BorrowingResponse response, String userId);

    /**
     * Publishes audit events for borrowing deletion.
     * @param borrowing the borrowing entity being deleted
     * @param userId the ID of the user who deleted the borrowing
     */
    void publishBorrowingDeleted(Borrowing borrowing, String userId);

    /**
     * Publishes audit events for borrowing access.
     * @param borrowing the borrowing entity being accessed
     * @param userId the ID of the user accessing the borrowing
     * @param accessType the type of access (e.g., "VIEW", "LIST")
     */
    void publishBorrowingAccessed(Borrowing borrowing, String userId, String accessType);
}