package com.library.user.repository;

import com.library.user.model.LibraryCard;
import com.library.user.repository.custom.LibraryCardRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryCardRepository extends JpaRepository<LibraryCard, Long>, LibraryCardRepositoryCustom {
    
    /**
     * Find all library cards for a user
     * @param userId the user ID
     * @return list of library cards
     */
    List<LibraryCard> findByUserId(Long userId);
    
    /**
     * Find all library cards with a specific status
     * @param status the status
     * @return list of library cards
     */
    List<LibraryCard> findByStatus(String status);
    
    /**
     * Find active library cards for a user
     * @param userId the user ID
     * @param status the status (ACTIVE)
     * @return optional library card
     */
    Optional<LibraryCard> findByUserIdAndStatus(Long userId, String status);
    
    /**
     * Find library cards that are expiring soon (within the specified days)
     * @param expiryDateStart start of date range
     * @param expiryDateEnd end of date range
     * @param status the status (ACTIVE)
     * @return list of library cards
     */
    @Query("SELECT lc FROM LibraryCard lc WHERE lc.expiryDate BETWEEN :startDate AND :endDate AND lc.status = :status")
    List<LibraryCard> findCardsByExpiryDateBetweenAndStatus(
            @Param("startDate") LocalDate expiryDateStart,
            @Param("endDate") LocalDate expiryDateEnd,
            @Param("status") String status);
    
    /**
     * Find expired library cards that are still marked as active
     * @param currentDate the current date
     * @param status the status (ACTIVE)
     * @return list of library cards
     */
    List<LibraryCard> findByExpiryDateBeforeAndStatus(LocalDate currentDate, String status);
}
