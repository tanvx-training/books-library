package com.library.user.infrastructure.persistence.impl;

import com.library.user.domain.model.user.Email;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
            UserJpaEntity jpaEntity = userEntityMapper.toJpaEntity(user);
            UserJpaEntity savedEntity = userJpaRepository.save(jpaEntity);
            return userEntityMapper.toDomainEntity(savedEntity);
        } catch (DataAccessException e) {
            log.error("Error saving user", e);
            throw new UserPersistenceException("Failed to save user", e);
        } catch (Exception e) {
            log.error("Unexpected error when saving user", e);
            throw new UserPersistenceException("Unexpected error when saving user", e);
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
    public List<User> findAll() {
        try {
            return userJpaRepository.findAll().stream()
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
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by("username"));
            return userJpaRepository.findAll(pageRequest).stream()
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
            throw new UserPersistenceException("Failed to delete user", e);
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
}