package com.library.notification.business;

import com.library.notification.business.dto.request.CreateNotificationRequest;
import com.library.notification.business.dto.request.NotificationSearchRequest;
import com.library.notification.business.dto.response.NotificationResponse;
import com.library.notification.business.dto.response.PagedNotificationResponse;
import com.library.notification.repository.enums.NotificationStatus;

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