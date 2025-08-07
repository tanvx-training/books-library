package com.library.notification.repository;

import com.library.notification.repository.entity.NotificationPreferences;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationPreferencesRepository extends JpaRepository<NotificationPreferences, Long> {

    Optional<NotificationPreferences> findByUserPublicId(UUID userPublicId);

    boolean existsByUserPublicId(UUID userPublicId);

    void deleteByUserPublicId(UUID userPublicId);
}