package com.library.notification.service;

import com.library.notification.dto.request.UpdatePreferencesRequest;
import com.library.notification.dto.response.NotificationPreferencesResponse;

import java.util.UUID;

public interface NotificationPreferencesService {

    NotificationPreferencesResponse createDefaultPreferences(UUID userPublicId);

    NotificationPreferencesResponse getPreferences(UUID userPublicId);

    NotificationPreferencesResponse updatePreferences(UUID userPublicId, UpdatePreferencesRequest request);

    boolean isNotificationTypeEnabled(UUID userPublicId, String notificationType);

    boolean isChannelEnabled(UUID userPublicId, String channel);

    void deletePreferences(UUID userPublicId);
}