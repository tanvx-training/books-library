package com.library.notification.repository.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_public_id", nullable = false, unique = true)
    @NotNull(message = "User public ID is required")
    private UUID userPublicId;

    @Column(name = "email_enabled", nullable = false)
    private Boolean emailEnabled = true;

    @Column(name = "sms_enabled", nullable = false)
    private Boolean smsEnabled = false;

    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled = true;

    @Column(name = "borrow_notification", nullable = false)
    private Boolean borrowNotification = true;

    @Column(name = "return_reminder", nullable = false)
    private Boolean returnReminder = true;

    @Column(name = "overdue_notification", nullable = false)
    private Boolean overdueNotification = true;

    @Column(name = "reservation_notification", nullable = false)
    private Boolean reservationNotification = true;
}