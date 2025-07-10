package com.library.user.repository.custom;

import com.library.user.infrastructure.persistence.entity.LibraryCard;

import java.time.LocalDate;
import java.util.List;

/**
 * Custom repository interface for LibraryCard with advanced operations that need logging
 */
public interface LibraryCardRepositoryCustom {
    
    /**
     * Find library cards that are expiring within the specified date range
     * @param expiryDateStart start of date range
     * @param expiryDateEnd end of date range
     * @param status the status to filter by
     * @return list of library cards
     */
    List<LibraryCard> findCardsByExpiryDateBetweenAndStatus(
            LocalDate expiryDateStart, LocalDate expiryDateEnd, String status);
    
    /**
     * Find expired library cards with specific status
     * @param currentDate the current date
     * @param status the status to filter by
     * @return list of library cards
     */
    List<LibraryCard> findExpiredCardsByStatus(LocalDate currentDate, String status);
    
    /**
     * Count cards by status for admin analytics
     * @param status the status to count
     * @return count of cards
     */
    Long countCardsByStatus(String status);
    
    /**
     * Find all cards for a user with detailed information
     * @param userId the user ID
     * @return list of cards with complete information
     */
    List<LibraryCard> findCardsWithUserDetailsById(Long userId);
} 