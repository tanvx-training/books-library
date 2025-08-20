package com.library.notification.repository;

import com.library.notification.repository.Notification;
import com.library.notification.repository.NotificationStatus;
import com.library.notification.repository.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, JpaSpecificationExecutor<Notification> {

    Optional<Notification> findByPublicId(UUID publicId);

    Page<Notification> findByUserPublicIdOrderByCreatedAtDesc(UUID userPublicId, Pageable pageable);

    List<Notification> findByUserPublicIdAndStatus(UUID userPublicId, NotificationStatus status);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByType(NotificationType type);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.userPublicId = :userPublicId AND n.readAt IS NULL")
    Long countUnreadByUserPublicId(@Param("userPublicId") UUID userPublicId);

    Long countByStatus(NotificationStatus status);

    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.createdAt >= :fromDateTime ORDER BY n.createdAt ASC")
    List<Notification> findFailedNotificationsAfter(@Param("fromDateTime") LocalDateTime fromDateTime);

    @Query("SELECT n FROM Notification n WHERE n.userPublicId = :userPublicId " +
           "AND (:type IS NULL OR n.type = :type) " +
           "AND (:fromDate IS NULL OR n.createdAt >= :fromDate) " +
           "AND (:toDate IS NULL OR n.createdAt <= :toDate) " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserAndFilters(
            @Param("userPublicId") UUID userPublicId,
            @Param("type") NotificationType type,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}