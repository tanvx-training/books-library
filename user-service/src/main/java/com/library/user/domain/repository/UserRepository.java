package com.library.user.domain.repository;

import com.library.user.domain.model.user.Email;
import com.library.user.domain.model.user.KeycloakId;
import com.library.user.domain.model.user.User;
import com.library.user.domain.model.user.UserId;
import com.library.user.domain.model.user.Username;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(UserId id);

    Optional<User> findByEmail(Email email);

    Optional<User> findByUsername(Username username);
    
    Optional<User> findByKeycloakId(KeycloakId keycloakId);

    List<User> findAll();

    List<User> findAll(int page, int size);

    long count();

    void delete(User user);

    boolean existsByUsername(Username username);

    boolean existsByEmail(Email email);
    
    boolean existsByKeycloakId(KeycloakId keycloakId);
    
    // Specification-based queries
    List<User> findBySpecification(com.library.user.domain.specification.UserSpecification specification);
    
    List<User> findBySpecification(com.library.user.domain.specification.UserSpecification specification, 
                                  org.springframework.data.domain.Pageable pageable);
    
    long countBySpecification(com.library.user.domain.specification.UserSpecification specification);
    
    // Complex business queries
    List<User> findUsersWithOverdueBooks();
    
    List<User> findUsersEligibleForCardRenewal();
    
    List<User> findInactiveUsers(java.time.LocalDateTime since);
}