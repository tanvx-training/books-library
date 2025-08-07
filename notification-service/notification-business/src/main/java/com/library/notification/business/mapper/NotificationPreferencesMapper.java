package com.library.notification.business.mapper;

import com.library.notification.business.dto.request.UpdatePreferencesRequest;
import com.library.notification.business.dto.response.NotificationPreferencesResponse;
import com.library.notification.repository.entity.NotificationPreferences;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class NotificationPreferencesMapper {

    public NotificationPreferencesResponse toResponse(NotificationPreferences entity) {
        if (entity == null) {
            return null;
        }
        
        NotificationPreferencesResponse response = new NotificationPreferencesResponse();
        response.setUserPublicId(entity.getUserPublicId());
        response.setEmailEnabled(entity.getEmailEnabled());
        response.setSmsEnabled(entity.getSmsEnabled());
        response.setPushEnabled(entity.getPushEnabled());
        response.setBorrowNotification(entity.getBorrowNotification());
        response.setReturnReminder(entity.getReturnReminder());
        response.setOverdueNotification(entity.getOverdueNotification());
        response.setReservationNotification(entity.getReservationNotification());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        
        return response;
    }

    public void updateFromRequest(NotificationPreferences entity, UpdatePreferencesRequest request) {
        if (entity == null || request == null) {
            return;
        }
        
        entity.setEmailEnabled(request.getEmailEnabled());
        entity.setSmsEnabled(request.getSmsEnabled());
        entity.setPushEnabled(request.getPushEnabled());
        entity.setBorrowNotification(request.getBorrowNotification());
        entity.setReturnReminder(request.getReturnReminder());
        entity.setOverdueNotification(request.getOverdueNotification());
        entity.setReservationNotification(request.getReservationNotification());
    }

    public NotificationPreferences createDefaultPreferences(UUID userPublicId) {
        if (userPublicId == null) {
            return null;
        }
        
        NotificationPreferences entity = new NotificationPreferences();
        entity.setUserPublicId(userPublicId);
        entity.setEmailEnabled(true);
        entity.setSmsEnabled(false);
        entity.setPushEnabled(true);
        entity.setBorrowNotification(true);
        entity.setReturnReminder(true);
        entity.setOverdueNotification(true);
        entity.setReservationNotification(true);
        
        return entity;
    }
}