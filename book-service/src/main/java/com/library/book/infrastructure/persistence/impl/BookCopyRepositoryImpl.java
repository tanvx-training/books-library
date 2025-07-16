package com.library.book.infrastructure.persistence.impl;

import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.bookcopy.BookCopy;
import com.library.book.domain.model.bookcopy.BookCopyId;
import com.library.book.domain.model.bookcopy.BookCopyStatus;
import com.library.book.domain.repository.BookCopyRepository;
import com.library.book.infrastructure.exception.BookPersistenceException;
import com.library.book.infrastructure.persistence.entity.BookCopyJpaEntity;
import com.library.book.infrastructure.persistence.mapper.BookCopyEntityMapper;
import com.library.book.infrastructure.persistence.repository.BookCopyJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BookCopyRepository using JPA
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class BookCopyRepositoryImpl implements BookCopyRepository {
    
    private final BookCopyJpaRepository bookCopyJpaRepository;
    private final BookCopyEntityMapper bookCopyEntityMapper;
    
    @Override
    public BookCopy save(BookCopy bookCopy) {
        try {
            BookCopyJpaEntity entity = bookCopyEntityMapper.toJpaEntity(bookCopy);
            BookCopyJpaEntity savedEntity = bookCopyJpaRepository.save(entity);
            return bookCopyEntityMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving book copy", e);
            throw new BookPersistenceException("Failed to save book copy", e);
        }
    }
    
    @Override
    public Optional<BookCopy> findById(BookCopyId id) {
        try {
            return bookCopyJpaRepository.findById(id.getValue())
                .map(bookCopyEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding book copy by ID: {}", id.getValue(), e);
            throw new BookPersistenceException("Failed to find book copy by ID: " + id.getValue(), e);
        }
    }
    
    @Override
    public List<BookCopy> findByBookId(BookId bookId) {
        try {
            List<BookCopyJpaEntity> entities = bookCopyJpaRepository.findByBookIdAndDeleteFlgFalse(bookId.getValue());
            return entities.stream()
                .map(bookCopyEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding book copies by book ID: {}", bookId.getValue(), e);
            throw new BookPersistenceException("Failed to find book copies by book ID: " + bookId.getValue(), e);
        }
    }
    
    @Override
    public List<BookCopy> findAvailableCopiesByBookId(BookId bookId) {
        try {
            List<BookCopyJpaEntity> entities = bookCopyJpaRepository.findAvailableCopiesByBookId(bookId.getValue());
            return entities.stream()
                .map(bookCopyEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding available book copies by book ID: {}", bookId.getValue(), e);
            throw new BookPersistenceException("Failed to find available book copies by book ID: " + bookId.getValue(), e);
        }
    }
    
    @Override
    public Page<BookCopy> findByStatus(BookCopyStatus status, int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BookCopyJpaEntity> entityPage = bookCopyJpaRepository.findByStatusAndDeleteFlgFalse(status, pageable);
            return entityPage.map(bookCopyEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding book copies by status: {}", status, e);
            throw new BookPersistenceException("Failed to find book copies by status: " + status, e);
        }
    }
    
    @Override
    public List<BookCopy> findBorrowedByUser(String userKeycloakId) {
        try {
            List<BookCopyJpaEntity> entities = bookCopyJpaRepository.findBorrowedByUser(userKeycloakId);
            return entities.stream()
                .map(bookCopyEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding borrowed book copies by user: {}", userKeycloakId, e);
            throw new BookPersistenceException("Failed to find borrowed book copies by user: " + userKeycloakId, e);
        }
    }
    
    @Override
    public List<BookCopy> findOverdueCopies(LocalDateTime currentDate) {
        try {
            List<BookCopyJpaEntity> entities = bookCopyJpaRepository.findOverdueCopies(currentDate);
            return entities.stream()
                .map(bookCopyEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding overdue book copies", e);
            throw new BookPersistenceException("Failed to find overdue book copies", e);
        }
    }
    
    @Override
    public List<BookCopy> findCopiesDueSoon(LocalDateTime fromDate, LocalDateTime toDate) {
        try {
            List<BookCopyJpaEntity> entities = bookCopyJpaRepository.findCopiesDueSoon(fromDate, toDate);
            return entities.stream()
                .map(bookCopyEntityMapper::toDomainEntity)
                .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding book copies due soon", e);
            throw new BookPersistenceException("Failed to find book copies due soon", e);
        }
    }
    
    @Override
    public boolean existsByBookIdAndCopyNumber(BookId bookId, String copyNumber) {
        try {
            return bookCopyJpaRepository.existsByBookIdAndCopyNumberAndDeleteFlgFalse(bookId.getValue(), copyNumber);
        } catch (DataAccessException e) {
            log.error("Error checking if book copy exists: bookId={}, copyNumber={}", bookId.getValue(), copyNumber, e);
            throw new BookPersistenceException("Failed to check if book copy exists", e);
        }
    }
    
    @Override
    public long countAvailableCopiesByBookId(BookId bookId) {
        try {
            return bookCopyJpaRepository.countAvailableCopiesByBookId(bookId.getValue());
        } catch (DataAccessException e) {
            log.error("Error counting available book copies by book ID: {}", bookId.getValue(), e);
            throw new BookPersistenceException("Failed to count available book copies", e);
        }
    }
    
    @Override
    public long countByBookId(BookId bookId) {
        try {
            return bookCopyJpaRepository.countByBookIdAndDeleteFlgFalse(bookId.getValue());
        } catch (DataAccessException e) {
            log.error("Error counting book copies by book ID: {}", bookId.getValue(), e);
            throw new BookPersistenceException("Failed to count book copies", e);
        }
    }
    
    @Override
    public Page<BookCopy> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<BookCopyJpaEntity> entityPage = bookCopyJpaRepository.findByDeleteFlgFalse(pageable);
            return entityPage.map(bookCopyEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding all book copies with pagination", e);
            throw new BookPersistenceException("Failed to find all book copies with pagination", e);
        }
    }
    
    @Override
    public void delete(BookCopy bookCopy) {
        try {
            bookCopyJpaRepository.deleteById(bookCopy.getId().getValue());
        } catch (DataAccessException e) {
            log.error("Error deleting book copy with ID: {}", bookCopy.getId().getValue(), e);
            throw new BookPersistenceException("Failed to delete book copy with ID: " + bookCopy.getId().getValue(), e);
        }
    }
}