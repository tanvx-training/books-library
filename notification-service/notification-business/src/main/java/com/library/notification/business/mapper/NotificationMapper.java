package com.library.notification.business.mapper;

import com.library.notification.business.dto.request.CreateNotificationRequest;
import com.library.notification.business.dto.response.NotificationResponse;
import com.library.notification.business.dto.response.PagedNotificationResponse;
import com.library.notification.repository.entity.Notification;
import com.library.notification.repository.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {

    public Notification toEntity(CreateNotificationRequest request) {
        if (request == null) {
            return null;
        }
        
        Notification entity = new Notification();
        entity.setUserPublicId(request.getUserPublicId());
        entity.setTitle(request.getTitle());
        entity.setContent(request.getContent());
        entity.setType(request.getType());
        entity.setStatus(NotificationStatus.SENT);
        entity.setSentAt(LocalDateTime.now());
        
        return entity;
    }

    public NotificationResponse toResponse(Notification entity) {
        if (entity == null) {
            return null;
        }
        
        NotificationResponse response = new NotificationResponse();
        response.setPublicId(entity.getPublicId());
        response.setUserPublicId(entity.getUserPublicId());
        response.setTitle(entity.getTitle());
        response.setContent(entity.getContent());
        response.setType(entity.getType());
        response.setStatus(entity.getStatus());
        response.setSentAt(entity.getSentAt());
        response.setReadAt(entity.getReadAt());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        
        return response;
    }

    public List<NotificationResponse> toResponseList(List<Notification> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public PagedNotificationResponse toPagedResponse(Page<Notification> page) {
        if (page == null) {
            return null;
        }
        
        PagedNotificationResponse response = new PagedNotificationResponse();
        response.setContent(toResponseList(page.getContent()));
        response.setPage(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        response.setHasNext(page.hasNext());
        response.setHasPrevious(page.hasPrevious());
        return response;
    }

    public void updateStatus(Notification entity, NotificationStatus status) {
        if (entity != null && status != null) {
            entity.setStatus(status);
        }
    }

    public void markAsRead(Notification entity) {
        if (entity != null) {
            entity.setStatus(NotificationStatus.READ);
            entity.setReadAt(LocalDateTime.now());
        }
    }

    public void markAsDelivered(Notification entity) {
        if (entity != null) {
            entity.setStatus(NotificationStatus.DELIVERED);
        }
    }

    public void markAsFailed(Notification entity) {
        if (entity != null) {
            entity.setStatus(NotificationStatus.FAILED);
        }
    }
}