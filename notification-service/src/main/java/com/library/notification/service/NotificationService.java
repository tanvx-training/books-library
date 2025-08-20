package com.library.notification.service;

import com.library.notification.dto.request.CreateNotificationRequest;
import com.library.notification.dto.request.NotificationSearchRequest;
import com.library.notification.dto.response.NotificationResponse;
import com.library.notification.dto.response.PagedNotificationResponse;
import com.library.notification.repository.NotificationStatus;

import java.util.UUID;

public interface NotificationService {

    NotificationResponse createNotification(CreateNotificationRequest request);

    NotificationResponse getNotificationById(UUID publicId, UUID userPublicId);

    PagedNotificationResponse searchNotifications(NotificationSearchRequest searchRequest);

    NotificationResponse updateNotificationStatus(UUID publicId, NotificationStatus status, UUID userPublicId);

    NotificationResponse markAsRead(UUID publicId, UUID userPublicId);

    PagedNotificationResponse getUserNotifications(UUID userPublicId, int page, int size, String sortBy, String sortDirection);

    boolean shouldSendNotification(UUID userPublicId, String notificationType);
}