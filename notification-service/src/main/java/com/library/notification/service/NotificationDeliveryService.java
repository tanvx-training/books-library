package com.library.notification.service;

import com.library.notification.repository.Notification;
import com.library.notification.repository.NotificationType;

import java.util.UUID;

public interface NotificationDeliveryService {

    boolean deliverNotification(Notification notification);

    void updateDeliveryStatus(UUID notificationId, boolean delivered, String errorMessage);

    boolean retryDelivery(UUID notificationId);

    boolean sendEmailNotification(Notification notification);

    boolean sendSmsNotification(Notification notification);

    boolean sendPushNotification(Notification notification);

    boolean isTypeSupported(NotificationType type);
}