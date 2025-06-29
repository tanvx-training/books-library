package com.library.book.repository.custom.impl;

import com.library.book.model.BookCopy;
import com.library.book.repository.custom.BookCopyRepositoryCustom;
import com.library.book.utils.enums.BookCopyStatus;
import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Custom repository implementation for BookCopy with advanced database operations and comprehensive logging
 */
@Repository
@RequiredArgsConstructor
public class BookCopyRepositoryCustomImpl implements BookCopyRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = false, // Don't log collections - can be large
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 800L,
        messagePrefix = "BOOK_COPY_REPO_AVAILABLE",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "availability_filter=true",
            "status_filtering=true",
            "inventory_check=true"
        }
    )
    public List<BookCopy> findAvailableBookCopiesByBookId(Long bookId) {
        String jpql = "SELECT bc FROM BookCopy bc " +
                     "WHERE bc.book.id = :bookId " +
                     "AND bc.status = :status " +
                     "ORDER BY bc.copyNumber";

        return entityManager.createQuery(jpql, BookCopy.class)
                .setParameter("bookId", bookId)
                .setParameter("status", BookCopyStatus.AVAILABLE.name())
                .getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DATABASE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1200L,
        messagePrefix = "BOOK_COPY_REPO_STATISTICS",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "analytics=true",
            "aggregate_function=true",
            "reporting=true",
            "inventory_analytics=true"
        }
    )
    public Map<BookCopyStatus, Long> getBookCopyStatisticsByStatus() {
        String jpql = "SELECT bc.status, COUNT(bc) FROM BookCopy bc " +
                     "GROUP BY bc.status";

        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createQuery(jpql).getResultList();

        Map<BookCopyStatus, Long> statistics = new HashMap<>();
        
        for (Object[] result : results) {
            String statusStr = (String) result[0];
            Long count = (Long) result[1];
            try {
                BookCopyStatus status = BookCopyStatus.valueOf(statusStr);
                statistics.put(status, count);
            } catch (IllegalArgumentException e) {
                // Handle invalid status gracefully
                continue;
            }
        }

        return statistics;
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = false, // Don't log collections
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPY_REPO_BY_STATUSES",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "multi_status_filter=true",
            "dynamic_query=true",
            "inventory_filtering=true"
        }
    )
    public List<BookCopy> findBookCopiesByStatuses(List<BookCopyStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return List.of();
        }

        List<String> statusStrings = statuses.stream()
                .map(Enum::name)
                .collect(Collectors.toList());

        String jpql = "SELECT bc FROM BookCopy bc " +
                     "WHERE bc.status IN :statuses " +
                     "ORDER BY bc.book.title, bc.copyNumber";

        return entityManager.createQuery(jpql, BookCopy.class)
                .setParameter("statuses", statusStrings)
                .getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_COPY_REPO_BATCH_UPDATE",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "bulk_operation=true",
            "batch_processing=true",
            "status_update=true",
            "inventory_management=true",
            "performance_critical=true"
        }
    )
    public int batchUpdateBookCopyStatus(List<Long> bookCopyIds, BookCopyStatus newStatus) {
        if (bookCopyIds == null || bookCopyIds.isEmpty()) {
            return 0;
        }

        String jpql = "UPDATE BookCopy bc SET bc.status = :newStatus, bc.updatedAt = :updatedAt " +
                     "WHERE bc.id IN :bookCopyIds";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("newStatus", newStatus.name());
        query.setParameter("updatedAt", LocalDateTime.now());
        query.setParameter("bookCopyIds", bookCopyIds);

        return query.executeUpdate();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = false, // Don't log overdue lists - can be sensitive
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "BOOK_COPY_REPO_OVERDUE",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "overdue_tracking=true",
            "business_critical=true",
            "date_calculation=true",
            "borrowing_analysis=true"
        }
    )
    public List<BookCopy> findOverdueBookCopies(int daysOverdue) {
        LocalDateTime overdueDate = LocalDateTime.now().minusDays(daysOverdue);

        String jpql = "SELECT DISTINCT bc FROM BookCopy bc " +
                     "JOIN bc.borrowings b " +
                     "WHERE bc.status = :borrowedStatus " +
                     "AND b.returnDate IS NULL " +
                     "AND b.borrowDate < :overdueDate " +
                     "ORDER BY b.borrowDate";

        return entityManager.createQuery(jpql, BookCopy.class)
                .setParameter("borrowedStatus", BookCopyStatus.BORROWED.name())
                .setParameter("overdueDate", overdueDate)
                .getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DATABASE,
        resourceType = "BookCopy",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_COPY_REPO_AVAILABILITY_REPORT",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "analytics=true",
            "reporting=true",
            "availability_analysis=true",
            "inventory_report=true",
            "business_intelligence=true"
        }
    )
    public Map<String, Object> getBookCopyAvailabilityReport(Long bookId) {
        // Get total copies
        String totalQuery = "SELECT COUNT(bc) FROM BookCopy bc WHERE bc.book.id = :bookId";
        Long totalCopies = entityManager.createQuery(totalQuery, Long.class)
                .setParameter("bookId", bookId)
                .getSingleResult();

        // Get available copies
        String availableQuery = "SELECT COUNT(bc) FROM BookCopy bc " +
                               "WHERE bc.book.id = :bookId AND bc.status = :status";
        Long availableCopies = entityManager.createQuery(availableQuery, Long.class)
                .setParameter("bookId", bookId)
                .setParameter("status", BookCopyStatus.AVAILABLE.name())
                .getSingleResult();

        // Get borrowed copies
        Long borrowedCopies = entityManager.createQuery(availableQuery, Long.class)
                .setParameter("bookId", bookId)
                .setParameter("status", BookCopyStatus.BORROWED.name())
                .getSingleResult();

        // Get reserved copies
        Long reservedCopies = entityManager.createQuery(availableQuery, Long.class)
                .setParameter("bookId", bookId)
                .setParameter("status", BookCopyStatus.RESERVED.name())
                .getSingleResult();

        Map<String, Object> report = new HashMap<>();
        report.put("bookId", bookId);
        report.put("totalCopies", totalCopies);
        report.put("availableCopies", availableCopies);
        report.put("borrowedCopies", borrowedCopies);
        report.put("reservedCopies", reservedCopies);
        report.put("availabilityRate", totalCopies > 0 ? 
                   (double) availableCopies / totalCopies * 100 : 0.0);
        report.put("reportGeneratedAt", LocalDateTime.now());

        return report;
    }
} 