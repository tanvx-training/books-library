package com.library.loan.repository;

public enum ReservationStatus {
    PENDING,    // Waiting in queue for book to become available
    FULFILLED,  // Book is ready for pickup
    CANCELLED,  // Cancelled by user or system
    EXPIRED     // Expired due to non-pickup or timeout
}