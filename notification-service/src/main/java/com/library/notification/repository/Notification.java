package com.library.notification.repository;

import com.library.notification.repository.NotificationType;
import com.library.notification.repository.NotificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private UUID publicId = UUID.randomUUID();

    @Column(name = "user_public_id", nullable = false)
    @NotNull(message = "User public ID is required")
    private UUID userPublicId;

    @Column(name = "title", nullable = false, length = 255)
    @NotBlank(message = "Title is required")
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Content is required")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    @NotNull(message = "Notification type is required")
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @NotNull(message = "Notification status is required")
    private NotificationStatus status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;
}