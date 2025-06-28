package com.library.notification.model;

import com.library.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
@Entity
@Table(name = "notification_preferences")
public class NotificationPreferences extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "email_enabled")
    private boolean emailEnabled = true;

    @Column(name = "sms_enabled")
    private boolean smsEnabled = false;

    @Column(name = "push_enabled")
    private boolean pushEnabled = true;

    @Column(name = "borrow_notification")
    private boolean borrowNotification = true;

    @Column(name = "return_reminder")
    private boolean returnReminder = true;

    @Column(name = "overdue_notification")
    private boolean overdueNotification = true;

    @Column(name = "reservation_notification")
    private boolean reservationNotification = true;
}