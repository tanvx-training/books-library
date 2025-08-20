package com.library.notification.service;

import com.library.notification.dto.request.CreateNotificationRequest;
import com.library.notification.dto.request.NotificationSearchRequest;
import com.library.notification.dto.response.NotificationResponse;
import com.library.notification.dto.response.PagedNotificationResponse;
import com.library.notification.aop.NotificationAccessDeniedException;
import com.library.notification.aop.NotificationNotFoundException;
import com.library.notification.repository.NotificationPreferencesRepository;
import com.library.notification.repository.NotificationRepository;
import com.library.notification.repository.Notification;
import com.library.notification.repository.NotificationPreferences;
import com.library.notification.repository.NotificationStatus;
import com.library.notification.repository.NotificationSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferencesRepository preferencesRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public NotificationResponse createNotification(CreateNotificationRequest request) {
        log.info("Creating notification for user: {}", request.getUserPublicId());
        
        // Check if user should receive this type of notification
        if (!shouldSendNotification(request.getUserPublicId(), getNotificationTypeFromRequest(request))) {
            log.info("Notification not sent due to user preferences for user: {}", request.getUserPublicId());
            return null;
        }

        Notification entity = notificationMapper.toEntity(request);
        notificationRepository.save(entity);
        
        log.info("Notification created with ID: {} for user: {}", entity.getPublicId(), request.getUserPublicId());
        return notificationMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public NotificationResponse getNotificationById(UUID publicId, UUID userPublicId) {
        log.debug("Retrieving notification: {} for user: {}", publicId, userPublicId);
        
        Notification entity = notificationRepository.findByPublicId(publicId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + publicId));
        
        // Check if user has access to this notification
        if (!entity.getUserPublicId().equals(userPublicId)) {
            throw new NotificationAccessDeniedException("User does not have access to this notification");
        }
        
        return notificationMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedNotificationResponse searchNotifications(NotificationSearchRequest searchRequest) {
        log.debug("Searching notifications with criteria: {}", searchRequest);
        
        Specification<Notification> spec = buildSpecificationFromRequest(searchRequest);
        
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(searchRequest.getSortDirection()) ? Sort.Direction.DESC : Sort.Direction.ASC,
            searchRequest.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize(), sort);
        Page<Notification> page = notificationRepository.findAll(spec, pageable);
        
        return notificationMapper.toPagedResponse(page);
    }

    @Override
    public NotificationResponse updateNotificationStatus(UUID publicId, NotificationStatus status, UUID userPublicId) {
        log.info("Updating notification status: {} to {} for user: {}", publicId, status, userPublicId);

        Notification entity = notificationRepository.findByPublicId(publicId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + publicId));
        
        // Check if user has access to this notification
        if (!entity.getUserPublicId().equals(userPublicId)) {
            throw new NotificationAccessDeniedException("User does not have access to this notification");
        }
        
        notificationMapper.updateStatus(entity, status);
        Notification savedEntity = notificationRepository.save(entity);
        
        return notificationMapper.toResponse(savedEntity);
    }

    @Override
    public NotificationResponse markAsRead(UUID publicId, UUID userPublicId) {
        log.info("Marking notification as read: {} for user: {}", publicId, userPublicId);

        Notification entity = notificationRepository.findByPublicId(publicId)
            .orElseThrow(() -> new NotificationNotFoundException("Notification not found with ID: " + publicId));
        
        // Check if user has access to this notification
        if (!entity.getUserPublicId().equals(userPublicId)) {
            throw new NotificationAccessDeniedException("User does not have access to this notification");
        }
        
        notificationMapper.markAsRead(entity);
        Notification savedEntity = notificationRepository.save(entity);
        
        return notificationMapper.toResponse(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedNotificationResponse getUserNotifications(UUID userPublicId, int page, int size, String sortBy, String sortDirection) {
        log.debug("Retrieving notifications for user: {} (page: {}, size: {})", userPublicId, page, size);
        
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy
        );
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Notification> notificationPage = notificationRepository.findByUserPublicIdOrderByCreatedAtDesc(userPublicId, pageable);
        
        return notificationMapper.toPagedResponse(notificationPage);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean shouldSendNotification(UUID userPublicId, String notificationType) {
        log.debug("Checking notification preferences for user: {} and type: {}", userPublicId, notificationType);
        
        NotificationPreferences preferences = preferencesRepository.findByUserPublicId(userPublicId)
            .orElse(null);
        
        if (preferences == null) {
            log.warn("No preferences found for user: {}, allowing notification", userPublicId);
            return true; // Default to allowing notifications if no preferences found
        }
        
        // Check channel preferences first
        boolean channelEnabled = switch (notificationType.toLowerCase()) {
            case "email" -> preferences.getEmailEnabled();
            case "sms" -> preferences.getSmsEnabled();
            case "push" -> preferences.getPushEnabled();
            default -> true; // Default to enabled for unknown types
        };
        
        if (!channelEnabled) {
            return false;
        }
        
        // For specific notification types, we would need additional context
        // This is a simplified implementation
        return true;
    }


    private String getNotificationTypeFromRequest(CreateNotificationRequest request) {
        return request.getType().name().toLowerCase();
    }

    private Specification<Notification> buildSpecificationFromRequest(NotificationSearchRequest searchRequest) {
        Specification<Notification> spec = Specification.where(null);
        
        if (searchRequest.getUserPublicId() != null) {
            spec = spec.and(NotificationSpecification.hasUserPublicId(searchRequest.getUserPublicId()));
        }
        
        if (searchRequest.getType() != null) {
            spec = spec.and(NotificationSpecification.hasType(searchRequest.getType()));
        }
        
        if (searchRequest.getStatus() != null) {
            spec = spec.and(NotificationSpecification.hasStatus(searchRequest.getStatus()));
        }
        
        if (searchRequest.getFromDate() != null) {
            spec = spec.and(NotificationSpecification.createdAfter(searchRequest.getFromDate()));
        }
        
        if (searchRequest.getToDate() != null) {
            spec = spec.and(NotificationSpecification.createdBefore(searchRequest.getToDate()));
        }
        
        if (searchRequest.getTitleContains() != null) {
            spec = spec.and(NotificationSpecification.titleContains(searchRequest.getTitleContains()));
        }
        
        if (searchRequest.getContentContains() != null) {
            spec = spec.and(NotificationSpecification.contentContains(searchRequest.getContentContains()));
        }
        
        if (searchRequest.getIsRead() != null) {
            if (searchRequest.getIsRead()) {
                spec = spec.and(NotificationSpecification.isRead());
            } else {
                spec = spec.and(NotificationSpecification.isUnread());
            }
        }
        
        return spec;
    }
}