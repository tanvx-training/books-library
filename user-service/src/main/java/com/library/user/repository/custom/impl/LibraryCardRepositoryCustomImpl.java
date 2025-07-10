package com.library.user.repository.custom.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.user.infrastructure.persistence.entity.LibraryCard;
import com.library.user.repository.custom.LibraryCardRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Repository
public class LibraryCardRepositoryCustomImpl implements LibraryCardRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = false, // Don't log card collections - can be large
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "LIBRARY_CARD_REPO_EXPIRY_RANGE",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "date_range_filter=true",
            "expiry_management=true",
            "scheduled_job_support=true"
        }
    )
    public List<LibraryCard> findCardsByExpiryDateBetweenAndStatus(
            LocalDate expiryDateStart, LocalDate expiryDateEnd, String status) {
        
        String jpql = "SELECT lc FROM LibraryCard lc " +
                     "LEFT JOIN FETCH lc.user u " +
                     "WHERE lc.expiryDate BETWEEN :startDate AND :endDate " +
                     "AND lc.status = :status " +
                     "ORDER BY lc.expiryDate ASC";
        
        TypedQuery<LibraryCard> query = entityManager.createQuery(jpql, LibraryCard.class);
        query.setParameter("startDate", expiryDateStart);
        query.setParameter("endDate", expiryDateEnd);
        query.setParameter("status", status);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = false,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "LIBRARY_CARD_REPO_EXPIRED",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "expired_cards=true",
            "scheduled_job_support=true",
            "status_management=true"
        }
    )
    public List<LibraryCard> findExpiredCardsByStatus(LocalDate currentDate, String status) {
        
        String jpql = "SELECT lc FROM LibraryCard lc " +
                     "LEFT JOIN FETCH lc.user u " +
                     "WHERE lc.expiryDate < :currentDate " +
                     "AND lc.status = :status " +
                     "ORDER BY lc.expiryDate ASC";
        
        TypedQuery<LibraryCard> query = entityManager.createQuery(jpql, LibraryCard.class);
        query.setParameter("currentDate", currentDate);
        query.setParameter("status", status);
        
        return query.getResultList();
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "LIBRARY_CARD_REPO_COUNT_STATUS",
        customTags = {
            "layer=repository", 
            "analytics_query=true", 
            "count_operation=true",
            "admin_operation=true"
        }
    )
    public Long countCardsByStatus(String status) {
        
        String jpql = "SELECT COUNT(lc) FROM LibraryCard lc WHERE lc.status = :status";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("status", status);
        
        return query.getSingleResult();
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "LibraryCard",
        logArguments = true,
        logReturnValue = false, // Don't log detailed card collections
        logExecutionTime = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "LIBRARY_CARD_REPO_USER_DETAILS",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "join_fetch=true",
            "user_relationship=true",
            "detailed_lookup=true"
        }
    )
    public List<LibraryCard> findCardsWithUserDetailsById(Long userId) {
        
        String jpql = "SELECT lc FROM LibraryCard lc " +
                     "LEFT JOIN FETCH lc.user u " +
                     "WHERE lc.user.id = :userId " +
                     "ORDER BY lc.issueDate DESC";
        
        TypedQuery<LibraryCard> query = entityManager.createQuery(jpql, LibraryCard.class);
        query.setParameter("userId", userId);
        
        return query.getResultList();
    }
} 