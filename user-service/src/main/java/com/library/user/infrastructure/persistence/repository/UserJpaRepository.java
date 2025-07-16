package com.library.user.infrastructure.persistence.repository;

import com.library.user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByKeycloakId(String keycloakId);
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByUsername(String username);
    Optional<UserJpaEntity> findByKeycloakId(String keycloakId);
    
    // Additional query methods for enhanced functionality
    List<UserJpaEntity> findByUpdatedAtBefore(LocalDateTime dateTime);
    
    @Query("SELECT u FROM UserJpaEntity u WHERE u.deleteFlg = false AND u.updatedAt >= :since")
    List<UserJpaEntity> findActiveUsersSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(u) FROM UserJpaEntity u WHERE u.deleteFlg = false")
    long countActiveUsers();
}