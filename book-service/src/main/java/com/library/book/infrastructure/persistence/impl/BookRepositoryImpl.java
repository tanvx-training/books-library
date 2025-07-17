package com.library.book.infrastructure.persistence.impl;

import com.library.book.domain.model.book.Book;
import com.library.book.domain.model.book.BookId;
import com.library.book.domain.model.category.CategoryId;
import com.library.book.domain.repository.BookRepository;
import com.library.book.infrastructure.exception.BookPersistenceException;
import com.library.book.infrastructure.persistence.entity.BookEntity;
import com.library.book.infrastructure.persistence.mapper.BookEntityMapper;
import com.library.book.infrastructure.persistence.repository.BookJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final BookJpaRepository bookJpaRepository;
    private final BookEntityMapper bookEntityMapper;

    @Override
    public Book save(Book book) {
        try {
            BookEntity entity = bookEntityMapper.toJpaEntity(book);
            bookJpaRepository.save(entity);
            return bookEntityMapper.toDomainEntity(entity);
        } catch (DataAccessException e) {
            log.error("Error saving book", e);
            throw new BookPersistenceException("Failed to save book", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving book", e);
            throw new BookPersistenceException("Unexpected error when saving book", e);
        }
    }

    @Override
    public Optional<Book> findById(BookId id) {
        try {
            return bookJpaRepository.findById(id.getValue())
                    .map(bookEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding book by ID: {}", id.getValue(), e);
            throw new BookPersistenceException("Failed to find book by ID: " + id.getValue(), e);
        }
    }

    @Override
    public Page<Book> findAll(int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title"));
            return bookJpaRepository.findAllByDeleteFlg(false, pageRequest)
                    .map(bookEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding all books", e);
            throw new BookPersistenceException("Failed to find all books", e);
        }
    }

    @Override
    public Page<Book> findAllByTitle(String title, int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title"));
            return bookJpaRepository.findAllByTitleContainingIgnoreCaseAndDeleteFlg(title, false, pageRequest)
                    .map(bookEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding books by title: {}", title, e);
            throw new BookPersistenceException("Failed to find books by title", e);
        }
    }

    @Override
    public Page<Book> findAllByCategories(List<CategoryId> categoryIds, int page, int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("title"));
            List<Long> categoryIdValues = categoryIds.stream()
                    .map(CategoryId::getValue)
                    .collect(Collectors.toList());
            return bookJpaRepository.findAllByCategoryIdsAndDeleteFlg(categoryIdValues, false, pageRequest)
                    .map(bookEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding books by categories", e);
            throw new BookPersistenceException("Failed to find books by categories", e);
        }
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        try {
            return bookJpaRepository.existsByIsbn(isbn);
        } catch (DataAccessException e) {
            log.error("Error checking if book exists by ISBN: {}", isbn, e);
            throw new BookPersistenceException("Failed to check if book exists by ISBN", e);
        }
    }

    @Override
    public long count() {
        try {
            return bookJpaRepository.count();
        } catch (DataAccessException e) {
            log.error("Error counting books", e);
            throw new BookPersistenceException("Failed to count books", e);
        }
    }

    @Override
    public void delete(Book book) {
        try {
            // Soft delete
            BookEntity entity = bookEntityMapper.toJpaEntity(book);
            entity.setDeleteFlg(true);
            bookJpaRepository.save(entity);
        } catch (DataAccessException e) {
            log.error("Error deleting book", e);
            throw new BookPersistenceException("Failed to delete book", e);
        }
    }
} 