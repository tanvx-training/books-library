package com.library.user.repository.custom.impl;

import com.library.common.aop.annotation.Loggable;
import com.library.common.enums.LogLevel;
import com.library.common.enums.OperationType;
import com.library.user.infrastructure.persistence.entity.User;
import com.library.user.repository.custom.UserRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepositoryCustomImpl implements UserRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = false, // Don't log user details for security
        logExecutionTime = true,
        sanitizeSensitiveData = true,
        performanceThresholdMs = 800L,
        messagePrefix = "USER_REPO_EMAIL_WITH_ROLES",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "security_operation=true",
            "role_fetch=true",
            "authentication=true"
        }
    )
    public Optional<User> findByEmailWithRoles(String email) {
        
        String jpql = "SELECT u FROM User u " +
                     "LEFT JOIN FETCH u.roles r " +
                     "WHERE u.email = :email AND u.deleteFlg = false";
        
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("email", email);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Loggable(
        level = LogLevel.ADVANCED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = false, // Don't log user details for security
        logExecutionTime = true,
        sanitizeSensitiveData = true,
        performanceThresholdMs = 800L,
        messagePrefix = "USER_REPO_USERNAME_WITH_ROLES",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "security_operation=true",
            "role_fetch=true",
            "authentication=true"
        }
    )
    public Optional<User> findByUsernameWithRoles(String username) {
        
        String jpql = "SELECT u FROM User u " +
                     "LEFT JOIN FETCH u.roles r " +
                     "WHERE u.username = :username AND u.deleteFlg = false";
        
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("username", username);
        
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        sanitizeSensitiveData = true,
        performanceThresholdMs = 500L,
        messagePrefix = "USER_REPO_EMAIL_AVAILABILITY",
        customTags = {
            "layer=repository", 
            "availability_check=true", 
            "registration_flow=true",
            "uniqueness_validation=true"
        }
    )
    public boolean isEmailAvailable(String email) {
        
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.deleteFlg = false";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("email", email);
        
        return query.getSingleResult() == 0;
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = true,
        logExecutionTime = true,
        performanceThresholdMs = 500L,
        messagePrefix = "USER_REPO_USERNAME_AVAILABILITY",
        customTags = {
            "layer=repository", 
            "availability_check=true", 
            "registration_flow=true",
            "uniqueness_validation=true"
        }
    )
    public boolean isUsernameAvailable(String username) {
        
        String jpql = "SELECT COUNT(u) FROM User u WHERE u.username = :username AND u.deleteFlg = false";
        
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
        query.setParameter("username", username);
        
        return query.getSingleResult() == 0;
    }

    @Override
    @Loggable(
        level = LogLevel.DETAILED,
        operationType = OperationType.READ,
        resourceType = "User",
        logArguments = true,
        logReturnValue = false, // Don't log user collections for security
        logExecutionTime = true,
        performanceThresholdMs = 1500L,
        messagePrefix = "USER_REPO_BY_ROLE",
        customTags = {
            "layer=repository", 
            "custom_query=true", 
            "admin_operation=true",
            "role_filter=true",
            "join_query=true"
        }
    )
    public List<User> findUsersByRoleName(String roleName) {
        
        String jpql = "SELECT DISTINCT u FROM User u " +
                     "LEFT JOIN FETCH u.roles r " +
                     "WHERE r.name = :roleName AND u.deleteFlg = false " +
                     "ORDER BY u.createdAt DESC";
        
        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("roleName", roleName);
        
        return query.getResultList();
    }
} 