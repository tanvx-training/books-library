package com.library.book.application.dto.response;

import com.library.book.domain.model.bookcopy.BookCondition;
import com.library.book.domain.model.bookcopy.BookCopyStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for book copy information
 */
@Data
@Builder
public class BookCopyResponse {
    
    private Long id;
    private Long bookId;
    private String copyNumber;
    private BookCopyStatus status;
    private BookCondition condition;
    private String location;
    private LocalDateTime acquiredDate;
    private String currentBorrowerKeycloakId;
    private LocalDateTime borrowedDate;
    private LocalDateTime dueDate;
    private boolean isOverdue;
    
    // Additional computed fields
    public String getStatusDescription() {
        return status != null ? status.getDescription() : null;
    }
    
    public String getConditionDescription() {
        return condition != null ? condition.getDescription() : null;
    }
    
    public boolean isAvailableForBorrowing() {
        return status != null && status.isAvailableForBorrowing() && 
               condition != null && condition.isAcceptableForLending();
    }
}