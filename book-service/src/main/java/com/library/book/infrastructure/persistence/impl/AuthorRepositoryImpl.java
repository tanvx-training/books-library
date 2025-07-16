package com.library.book.infrastructure.persistence.impl;

import com.library.book.domain.model.author.Author;
import com.library.book.domain.model.author.AuthorId;
import com.library.book.domain.repository.AuthorRepository;
import com.library.book.infrastructure.exception.AuthorPersistenceException;
import com.library.book.infrastructure.persistence.entity.AuthorJpaEntity;
import com.library.book.infrastructure.persistence.mapper.AuthorEntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.library.book.infrastructure.persistence.repository.AuthorJpaRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class AuthorRepositoryImpl implements AuthorRepository {

    private final AuthorJpaRepository authorJpaRepository;
    private final AuthorEntityMapper authorEntityMapper;

    @Override
    public Author save(Author author) {
        try {
            AuthorJpaEntity entity = authorEntityMapper.toJpaEntity(author);
            authorJpaRepository.save(entity);
            return authorEntityMapper.toDomainEntity(entity);
        } catch (DataAccessException e) {
            log.error("Error saving author", e);
            throw new AuthorPersistenceException("Failed to save author", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving author", e);
            throw new AuthorPersistenceException("Unexpected error when saving author", e);
        }
    }

    @Override
    public Optional<Author> findById(AuthorId id) {
        try {
            return authorJpaRepository.findById(id.getValue())
                    .map(authorEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding author by ID: {}", id.getValue(), e);
            throw new AuthorPersistenceException("Failed to find author by ID: " + id.getValue(), e);
        }
    }

    @Override
    public Page<Author> findAll(Pageable pageable) {
        try {
            return authorJpaRepository.findAllByDeleteFlg(false, pageable)
                    .map(authorEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding all authors", e);
            throw new AuthorPersistenceException("Failed to find all authors", e);
        }
    }

    @Override
    public long count() {
        try {
            return authorJpaRepository.count();
        } catch (DataAccessException e) {
            log.error("Error counting authors", e);
            throw new AuthorPersistenceException("Failed to count authors", e);
        }
    }

    @Override
    public List<Author> findAll() {
        try {
            return authorJpaRepository.findAllByDeleteFlg(false).stream()
                    .map(authorEntityMapper::toDomainEntity)
                    .collect(java.util.stream.Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding all authors", e);
            throw new AuthorPersistenceException("Failed to find all authors", e);
        }
    }

    @Override
    public boolean existsByName(com.library.book.domain.model.author.AuthorName name) {
        try {
            return authorJpaRepository.existsByNameAndDeleteFlg(name.getValue(), false);
        } catch (DataAccessException e) {
            log.error("Error checking if author exists by name: {}", name.getValue(), e);
            throw new AuthorPersistenceException("Failed to check if author exists by name", e);
        }
    }

    @Override
    public void delete(Author author) {
        try {
            // Soft delete
            AuthorJpaEntity entity = authorEntityMapper.toJpaEntity(author);
            entity.setDeleteFlg(true);
            authorJpaRepository.save(entity);
        } catch (DataAccessException e) {
            log.error("Error deleting author", e);
            throw new AuthorPersistenceException("Failed to delete author", e);
        }
    }
}
