package com.library.loan.repository.specification;

import com.library.loan.repository.entity.Borrowing;
import com.library.loan.repository.enums.BorrowingStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Specification class for dynamic query building for Borrowing entities.
 * Provides methods to create complex queries with multiple filtering criteria.
 */
public class BorrowingSpecification {

    /**
     * Creates a specification to filter borrowings that are not soft deleted.
     */
    public static Specification<Borrowing> isNotDeleted() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNull(root.get("deletedAt"));
    }

    /**
     * Creates a specification to filter borrowings by status.
     */
    public static Specification<Borrowing> hasStatus(BorrowingStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Creates a specification to filter borrowings by keyword search.
     * Searches in related user and book information (requires joins).
     */
    public static Specification<Borrowing> hasKeyword(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            String likePattern = "%" + keyword.toLowerCase() + "%";
            
            // For now, we'll search by user ID and book copy ID as strings
            // In a real implementation, you might want to join with user and book tables
            List<Predicate> predicates = new ArrayList<>();
            
            // Search by user ID (converted to string)
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(criteriaBuilder.toString(root.get("userId"))), 
                likePattern
            ));
            
            // Search by book copy ID (converted to string)
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(criteriaBuilder.toString(root.get("bookCopyId"))), 
                likePattern
            ));
            
            // Search by public ID (converted to string)
            predicates.add(criteriaBuilder.like(
                criteriaBuilder.lower(criteriaBuilder.toString(root.get("publicId"))), 
                likePattern
            ));
            
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification to filter borrowings by borrow date range.
     */
    public static Specification<Borrowing> borrowDateBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("borrowDate"), fromDate));
            }
            
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("borrowDate"), toDate));
            }
            
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification to filter borrowings by due date range.
     */
    public static Specification<Borrowing> dueDateBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dueDate"), fromDate));
            }
            
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dueDate"), toDate));
            }
            
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification to filter borrowings by return date range.
     */
    public static Specification<Borrowing> returnDateBetween(LocalDate fromDate, LocalDate toDate) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (fromDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("returnDate"), fromDate));
            }
            
            if (toDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("returnDate"), toDate));
            }
            
            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification to filter borrowings by user ID.
     */
    public static Specification<Borrowing> hasUserId(Long userId) {
        return (root, query, criteriaBuilder) -> {
            if (userId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("userId"), userId);
        };
    }

    /**
     * Creates a specification to filter borrowings by book copy ID.
     */
    public static Specification<Borrowing> hasBookCopyId(Long bookCopyId) {
        return (root, query, criteriaBuilder) -> {
            if (bookCopyId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("bookCopyId"), bookCopyId);
        };
    }

    /**
     * Creates a specification to filter overdue borrowings.
     */
    public static Specification<Borrowing> isOverdue() {
        return (root, query, criteriaBuilder) -> {
            LocalDate today = LocalDate.now();
            return criteriaBuilder.and(
                criteriaBuilder.lessThan(root.get("dueDate"), today),
                criteriaBuilder.equal(root.get("status"), BorrowingStatus.ACTIVE)
            );
        };
    }

    /**
     * Creates a specification to filter borrowings that are due within a certain number of days.
     */
    public static Specification<Borrowing> isDueWithinDays(int days) {
        return (root, query, criteriaBuilder) -> {
            LocalDate today = LocalDate.now();
            LocalDate futureDate = today.plusDays(days);
            return criteriaBuilder.and(
                criteriaBuilder.between(root.get("dueDate"), today, futureDate),
                criteriaBuilder.equal(root.get("status"), BorrowingStatus.ACTIVE)
            );
        };
    }

    /**
     * Combines multiple specifications with AND logic.
     */
    public static Specification<Borrowing> combineWithAnd(Specification<Borrowing>... specifications) {
        Specification<Borrowing> result = Specification.where(null);
        for (Specification<Borrowing> spec : specifications) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }

    /**
     * Combines multiple specifications with OR logic.
     */
    public static Specification<Borrowing> combineWithOr(Specification<Borrowing>... specifications) {
        Specification<Borrowing> result = null;
        for (Specification<Borrowing> spec : specifications) {
            if (spec != null) {
                if (result == null) {
                    result = spec;
                } else {
                    result = result.or(spec);
                }
            }
        }
        return result != null ? result : Specification.where(null);
    }

    /**
     * Creates a specification to filter borrowings by multiple statuses.
     */
    public static Specification<Borrowing> hasStatusIn(List<BorrowingStatus> statuses) {
        return (root, query, criteriaBuilder) -> {
            if (statuses == null || statuses.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    /**
     * Creates a specification to filter borrowings by public ID.
     */
    public static Specification<Borrowing> hasPublicId(UUID publicId) {
        return (root, query, criteriaBuilder) -> {
            if (publicId == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("publicId"), publicId);
        };
    }

    /**
     * Creates a specification to filter borrowings that have been returned.
     */
    public static Specification<Borrowing> isReturned() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), BorrowingStatus.RETURNED);
    }

    /**
     * Creates a specification to filter borrowings that are currently active.
     */
    public static Specification<Borrowing> isActive() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), BorrowingStatus.ACTIVE);
    }

    /**
     * Creates a specification to filter borrowings that are lost.
     */
    public static Specification<Borrowing> isLost() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), BorrowingStatus.LOST);
    }

    /**
     * Creates a specification to filter borrowings that are cancelled.
     */
    public static Specification<Borrowing> isCancelled() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.equal(root.get("status"), BorrowingStatus.CANCELLED);
    }

    /**
     * Creates a specification for comprehensive search with all common filters.
     * This is a convenience method that combines multiple filters commonly used together.
     */
    public static Specification<Borrowing> searchWithFilters(
            String keyword,
            BorrowingStatus status,
            LocalDate borrowDateFrom,
            LocalDate borrowDateTo,
            LocalDate dueDateFrom,
            LocalDate dueDateTo,
            Long userId,
            Long bookCopyId) {
        
        return combineWithAnd(
            isNotDeleted(),
            hasKeyword(keyword),
            hasStatus(status),
            borrowDateBetween(borrowDateFrom, borrowDateTo),
            dueDateBetween(dueDateFrom, dueDateTo),
            hasUserId(userId),
            hasBookCopyId(bookCopyId)
        );
    }
}