package com.library.user.infrastructure.persistence.impl;

import com.library.user.domain.model.librarycard.LibraryCard;
import com.library.user.domain.model.librarycard.LibraryCardId;
import com.library.user.domain.model.user.UserId;
import com.library.user.domain.repository.LibraryCardRepository;
import com.library.user.infrastructure.exception.UserPersistenceException;
import com.library.user.infrastructure.persistence.entity.LibraryCardJpaEntity;
import com.library.user.infrastructure.persistence.mapper.LibraryCardEntityMapper;
import com.library.user.infrastructure.persistence.repository.LibraryCardJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LibraryCardRepositoryImpl implements LibraryCardRepository {

    private final LibraryCardJpaRepository libraryCardJpaRepository;
    private final LibraryCardEntityMapper libraryCardEntityMapper;

    @Override
    public LibraryCard save(LibraryCard libraryCard) {
        try {
            LibraryCardJpaEntity entity = libraryCardEntityMapper.toJpaEntity(libraryCard);
            LibraryCardJpaEntity savedEntity = libraryCardJpaRepository.save(entity);
            return libraryCardEntityMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving library card", e);
            throw new UserPersistenceException("Failed to save library card", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving library card", e);
            throw new UserPersistenceException("Unexpected error when saving library card", e);
        }
    }

    @Override
    public Optional<LibraryCard> findById(LibraryCardId id) {
        try {
            return libraryCardJpaRepository.findById(id.getValue())
                    .map(libraryCardEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding library card by ID: {}", id.getValue(), e);
            throw new UserPersistenceException("Failed to find library card by ID: " + id.getValue(), e);
        }
    }

    @Override
    public List<LibraryCard> findByUserId(UserId userId) {
        try {
            return libraryCardJpaRepository.findByUserId(userId.getValue()).stream()
                    .map(libraryCardEntityMapper::toDomainEntity)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding library cards by user ID: {}", userId.getValue(), e);
            throw new UserPersistenceException("Failed to find library cards by user ID: " + userId.getValue(), e);
        }
    }

    @Override
    public void delete(LibraryCard libraryCard) {
        try {
            libraryCardJpaRepository.deleteById(libraryCard.getId().getValue());
        } catch (DataAccessException e) {
            log.error("Error deleting library card with ID: {}", libraryCard.getId().getValue(), e);
            throw new UserPersistenceException("Failed to delete library card", e);
        }
    }
}