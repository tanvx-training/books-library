package com.library.user.infrastructure.persistence.impl;

import com.library.user.domain.exception.UserNotFoundException;
import com.library.user.domain.model.user.Email;
import com.library.user.domain.model.user.KeycloakId;
import com.library.user.domain.model.user.User;
import com.library.user.domain.model.user.UserId;
import com.library.user.domain.model.user.Username;
import com.library.user.domain.repository.UserRepository;
import com.library.user.infrastructure.exception.UserPersistenceException;
import com.library.user.infrastructure.persistence.entity.UserJpaEntity;
import com.library.user.infrastructure.persistence.mapper.UserEntityMapper;
import com.library.user.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final UserEntityMapper userEntityMapper;

    @Override
    public User save(User user) {
        try {
            UserJpaEntity userJpaEntity = userEntityMapper.toJpaEntity(user);
            UserJpaEntity savedEntity = userJpaRepository.save(userJpaEntity);
            return userEntityMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving user", e);
            throw new UserPersistenceException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(UserId id) {
        try {
            return userJpaRepository.findById(id.getValue())
                    .map(userEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding user by ID: {}", id.getValue(), e);
            throw new UserPersistenceException("Failed to find user by ID: " + id.getValue(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        try {
            return userJpaRepository.findByEmail(email.getValue())
                    .map(userEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding user by email: {}", email.getValue(), e);
            throw new UserPersistenceException("Failed to find user by email: " + email.getValue(), e);
        }
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        try {
            return userJpaRepository.findByUsername(username.getValue())
                    .map(userEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding user by username: {}", username.getValue(), e);
            throw new UserPersistenceException("Failed to find user by username: " + username.getValue(), e);
        }
    }
    
    @Override
    public Optional<User> findByKeycloakId(KeycloakId keycloakId) {
        try {
            return userJpaRepository.findByKeycloakId(keycloakId.getValue())
                    .map(userEntityMapper::toDomainEntity);
        } catch (DataAccessException e) {
            log.error("Error finding user by keycloakId: {}", keycloakId.getValue(), e);
            throw new UserPersistenceException("Failed to find user by keycloakId: " + keycloakId.getValue(), e);
        }
    }

    @Override
    public List<User> findAll() {
        try {
            List<UserJpaEntity> userJpaEntities = userJpaRepository.findAll();
            return userJpaEntities.stream()
                    .map(userEntityMapper::toDomainEntity)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding all users", e);
            throw new UserPersistenceException("Failed to find all users", e);
        }
    }

    @Override
    public List<User> findAll(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<UserJpaEntity> userPage = userJpaRepository.findAll(pageable);
            return userPage.getContent().stream()
                    .map(userEntityMapper::toDomainEntity)
                    .collect(Collectors.toList());
        } catch (DataAccessException e) {
            log.error("Error finding users with pagination", e);
            throw new UserPersistenceException("Failed to find users with pagination", e);
        }
    }

    @Override
    public long count() {
        try {
            return userJpaRepository.count();
        } catch (DataAccessException e) {
            log.error("Error counting users", e);
            throw new UserPersistenceException("Failed to count users", e);
        }
    }

    @Override
    public void delete(User user) {
        try {
            userJpaRepository.deleteById(user.getId().getValue());
        } catch (DataAccessException e) {
            log.error("Error deleting user with ID: {}", user.getId().getValue(), e);
            throw new UserPersistenceException("Failed to delete user with ID: " + user.getId().getValue(), e);
        }
    }

    @Override
    public boolean existsByUsername(Username username) {
        try {
            return userJpaRepository.existsByUsername(username.getValue());
        } catch (DataAccessException e) {
            log.error("Error checking if username exists: {}", username.getValue(), e);
            throw new UserPersistenceException("Failed to check if username exists", e);
        }
    }

    @Override
    public boolean existsByEmail(Email email) {
        try {
            return userJpaRepository.existsByEmail(email.getValue());
        } catch (DataAccessException e) {
            log.error("Error checking if email exists: {}", email.getValue(), e);
            throw new UserPersistenceException("Failed to check if email exists", e);
        }
    }
    
    @Override
    public boolean existsByKeycloakId(KeycloakId keycloakId) {
        try {
            return userJpaRepository.existsByKeycloakId(keycloakId.getValue());
        } catch (DataAccessException e) {
            log.error("Error checking if keycloakId exists: {}", keycloakId.getValue(), e);
            throw new UserPersistenceException("Failed to check if keycloakId exists", e);
        }
    }
}