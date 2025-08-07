package com.library.notification.business.impl;

import com.library.notification.business.NotificationPreferencesService;
import com.library.notification.business.dto.request.UpdatePreferencesRequest;
import com.library.notification.business.dto.response.NotificationPreferencesResponse;
import com.library.notification.business.exception.NotificationPreferencesNotFoundException;
import com.library.notification.business.mapper.NotificationPreferencesMapper;
import com.library.notification.repository.NotificationPreferencesRepository;
import com.library.notification.repository.entity.NotificationPreferences;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationPreferencesServiceImpl implements NotificationPreferencesService {

    private final NotificationPreferencesRepository preferencesRepository;
    private final NotificationPreferencesMapper preferencesMapper;

    @Override
    public NotificationPreferencesResponse createDefaultPreferences(UUID userPublicId) {
        log.info("Creating default notification preferences for user: {}", userPublicId);
        
        try {
            // Check if preferences already exist
            if (preferencesRepository.findByUserPublicId(userPublicId).isPresent()) {
                log.warn("Preferences already exist for user: {}", userPublicId);
                return getPreferences(userPublicId);
            }
            
            NotificationPreferences entity = preferencesMapper.createDefaultPreferences(userPublicId);
            NotificationPreferences savedEntity = preferencesRepository.save(entity);
            
            log.info("Default preferences created for user: {}", userPublicId);
            return preferencesMapper.toResponse(savedEntity);
            
        } catch (DataIntegrityViolationException e) {
            log.warn("Preferences already exist for user: {} (concurrent creation)", userPublicId);
            return getPreferences(userPublicId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationPreferencesResponse getPreferences(UUID userPublicId) {
        log.debug("Retrieving notification preferences for user: {}", userPublicId);
        
        NotificationPreferences entity = preferencesRepository.findByUserPublicId(userPublicId)
            .orElseThrow(() -> new NotificationPreferencesNotFoundException(
                "Notification preferences not found for user: " + userPublicId));
        
        return preferencesMapper.toResponse(entity);
    }

    @Override
    public NotificationPreferencesResponse updatePreferences(UUID userPublicId, UpdatePreferencesRequest request) {
        log.info("Updating notification preferences for user: {}", userPublicId);
        
        NotificationPreferences entity = preferencesRepository.findByUserPublicId(userPublicId)
            .orElseThrow(() -> new NotificationPreferencesNotFoundException(
                "Notification preferences not found for user: " + userPublicId));
        
        // Validate preferences before updating
        validatePreferencesRequest(request);
        
        preferencesMapper.updateFromRequest(entity, request);
        NotificationPreferences savedEntity = preferencesRepository.save(entity);
        
        log.info("Preferences updated for user: {}", userPublicId);
        return preferencesMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isNotificationTypeEnabled(UUID userPublicId, String notificationType) {
        log.debug("Checking if notification type '{}' is enabled for user: {}", notificationType, userPublicId);
        
        NotificationPreferences preferences = preferencesRepository.findByUserPublicId(userPublicId)
            .orElse(null);
        
        if (preferences == null) {
            log.warn("No preferences found for user: {}, defaulting to enabled", userPublicId);
            return true; // Default to enabled if no preferences found
        }
        
        return switch (notificationType.toLowerCase()) {
            case "borrow", "borrow_notification" -> preferences.getBorrowNotification();
            case "return", "return_reminder" -> preferences.getReturnReminder();
            case "overdue", "overdue_notification" -> preferences.getOverdueNotification();
            case "reservation", "reservation_notification" -> preferences.getReservationNotification();
            default -> {
                log.warn("Unknown notification type: {}, defaulting to enabled", notificationType);
                yield true;
            }
        };
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isChannelEnabled(UUID userPublicId, String channel) {
        log.debug("Checking if channel '{}' is enabled for user: {}", channel, userPublicId);
        
        NotificationPreferences preferences = preferencesRepository.findByUserPublicId(userPublicId)
            .orElse(null);
        
        if (preferences == null) {
            log.warn("No preferences found for user: {}, defaulting to enabled", userPublicId);
            return true; // Default to enabled if no preferences found
        }
        
        return switch (channel.toUpperCase()) {
            case "EMAIL" -> preferences.getEmailEnabled();
            case "SMS" -> preferences.getSmsEnabled();
            case "PUSH" -> preferences.getPushEnabled();
            default -> {
                log.warn("Unknown channel: {}, defaulting to enabled", channel);
                yield true;
            }
        };
    }

    @Override
    public void deletePreferences(UUID userPublicId) {
        log.info("Deleting notification preferences for user: {}", userPublicId);
        
        NotificationPreferences entity = preferencesRepository.findByUserPublicId(userPublicId)
            .orElse(null);
        
        if (entity != null) {
            preferencesRepository.delete(entity);
            log.info("Preferences deleted for user: {}", userPublicId);
        } else {
            log.warn("No preferences found to delete for user: {}", userPublicId);
        }
    }

    private void validatePreferencesRequest(UpdatePreferencesRequest request) {
        if (request.getEmailEnabled() == null || request.getSmsEnabled() == null || 
            request.getPushEnabled() == null || request.getBorrowNotification() == null ||
            request.getReturnReminder() == null || request.getOverdueNotification() == null ||
            request.getReservationNotification() == null) {
            throw new IllegalArgumentException("All preference fields must be provided");
        }
        
        // Ensure at least one channel is enabled
        if (!request.getEmailEnabled() && !request.getSmsEnabled() && !request.getPushEnabled()) {
            throw new IllegalArgumentException("At least one notification channel must be enabled");
        }
        
        log.debug("Preferences request validation passed");
    }
}