package com.library.book.repository.custom.impl;

import com.library.book.model.Book;
import com.library.book.repository.custom.BookRepositoryCustom;
import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Custom repository implementation with advanced database operations and comprehensive logging
 */
@Repository
@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DATABASE,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = false, // Don't log result collections - can be large
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1000L,
        messagePrefix = "BOOK_REPO_COMPLEX_SEARCH",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "complex_criteria=true",
            "multi_table_join=true",
            "performance_critical=true"
        }
    )
    public List<Book> findBooksByComplexCriteria(String title, List<Long> authorIds, List<Long> categoryIds) {
        StringBuilder queryBuilder = new StringBuilder(
            "SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN FETCH b.authors a " +
            "LEFT JOIN FETCH b.categories c " +
            "LEFT JOIN FETCH b.publisher p " +
            "WHERE b.deleteFlg = false"
        );

        if (title != null && !title.trim().isEmpty()) {
            queryBuilder.append(" AND LOWER(b.title) LIKE LOWER(:title)");
        }
        
        if (authorIds != null && !authorIds.isEmpty()) {
            queryBuilder.append(" AND a.id IN :authorIds");
        }
        
        if (categoryIds != null && !categoryIds.isEmpty()) {
            queryBuilder.append(" AND c.id IN :categoryIds");
        }

        TypedQuery<Book> query = entityManager.createQuery(queryBuilder.toString(), Book.class);

        if (title != null && !title.trim().isEmpty()) {
            query.setParameter("title", "%" + title + "%");
        }
        if (authorIds != null && !authorIds.isEmpty()) {
            query.setParameter("authorIds", authorIds);
        }
        if (categoryIds != null && !categoryIds.isEmpty()) {
            query.setParameter("categoryIds", categoryIds);
        }

        return query.setMaxResults(100).getResultList(); // Limit results for performance
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 500L,
        messagePrefix = "BOOK_REPO_WITH_DETAILS",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "fetch_join=true",
            "single_entity=true",
            "eager_loading=true"
        }
    )
    public Optional<Book> findBookWithDetails(Long bookId) {
        String jpql = "SELECT b FROM Book b " +
                     "LEFT JOIN FETCH b.authors " +
                     "LEFT JOIN FETCH b.categories " +
                     "LEFT JOIN FETCH b.publisher " +
                     "LEFT JOIN FETCH b.bookCopies " +
                     "WHERE b.id = :bookId AND b.deleteFlg = false";

        try {
            Book book = entityManager.createQuery(jpql, Book.class)
                    .setParameter("bookId", bookId)
                    .getSingleResult();
            return Optional.of(book);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.SEARCH,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = false, // Search results can be large
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 2000L,
        messagePrefix = "BOOK_REPO_ADVANCED_SEARCH",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "full_text_search=true",
            "dynamic_query=true",
            "pagination=true",
            "multi_field_search=true"
        }
    )
    public Page<Book> searchBooksAdvanced(String query, List<String> fields, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT DISTINCT b FROM Book b ");
        StringBuilder whereClause = new StringBuilder("WHERE b.deleteFlg = false ");

        if (query != null && !query.trim().isEmpty()) {
            whereClause.append("AND (");
            
            if (fields.contains("title")) {
                whereClause.append("LOWER(b.title) LIKE LOWER(:query) OR ");
            }
            if (fields.contains("isbn")) {
                whereClause.append("LOWER(b.isbn) LIKE LOWER(:query) OR ");
            }
            if (fields.contains("description")) {
                whereClause.append("LOWER(b.description) LIKE LOWER(:query) OR ");
            }
            if (fields.contains("author")) {
                jpql.append("LEFT JOIN b.authors a ");
                whereClause.append("LOWER(a.name) LIKE LOWER(:query) OR ");
            }
            if (fields.contains("category")) {
                jpql.append("LEFT JOIN b.categories c ");
                whereClause.append("LOWER(c.name) LIKE LOWER(:query) OR ");
            }
            if (fields.contains("publisher")) {
                jpql.append("LEFT JOIN b.publisher p ");
                whereClause.append("LOWER(p.name) LIKE LOWER(:query) OR ");
            }
            
            // Remove last OR
            if (whereClause.toString().endsWith("OR ")) {
                whereClause.setLength(whereClause.length() - 3);
            }
            whereClause.append(")");
        }

        jpql.append(whereClause);

        // Count query for total elements
        String countJpql = jpql.toString().replace("SELECT DISTINCT b", "SELECT COUNT(DISTINCT b)");
        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql, Long.class);
        if (query != null && !query.trim().isEmpty()) {
            countQuery.setParameter("query", "%" + query + "%");
        }
        Long total = countQuery.getSingleResult();

        // Main query with pagination
        TypedQuery<Book> mainQuery = entityManager.createQuery(jpql.toString(), Book.class);
        if (query != null && !query.trim().isEmpty()) {
            mainQuery.setParameter("query", "%" + query + "%");
        }
        
        List<Book> books = mainQuery
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        return new PageImpl<>(books, pageable, total);
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.DATABASE,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 800L,
        messagePrefix = "BOOK_REPO_ANALYTICS",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "analytics=true",
            "aggregate_function=true",
            "reporting=true"
        }
    )
    public Long countBooksByCategory(Long categoryId) {
        String jpql = "SELECT COUNT(DISTINCT b) FROM Book b " +
                     "JOIN b.categories c " +
                     "WHERE c.id = :categoryId AND b.deleteFlg = false";

        return entityManager.createQuery(jpql, Long.class)
                .setParameter("categoryId", categoryId)
                .getSingleResult();
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.UPDATE,
        resourceType = "Book",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        includeInPerformanceMonitoring = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "BOOK_REPO_BULK_UPDATE",
        customTags = {
            "layer=repository",
            "database_operation=true",
            "bulk_operation=true",
            "transaction=write",
            "performance_critical=true",
            "batch_processing=true"
        }
    )
    public int updateBookAvailability(List<Long> bookIds, boolean available) {
        if (bookIds == null || bookIds.isEmpty()) {
            return 0;
        }

        String jpql = "UPDATE Book b SET b.available = :available, b.updatedAt = CURRENT_TIMESTAMP " +
                     "WHERE b.id IN :bookIds AND b.deleteFlg = false";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("available", available);
        query.setParameter("bookIds", bookIds);

        return query.executeUpdate();
    }
} 