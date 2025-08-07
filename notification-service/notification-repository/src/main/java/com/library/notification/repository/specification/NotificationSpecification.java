package com.library.notification.repository.specification;

import com.library.notification.repository.entity.Notification;
import com.library.notification.repository.enums.NotificationStatus;
import com.library.notification.repository.enums.NotificationType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationSpecification {

    public static Specification<Notification> hasUserPublicId(UUID userPublicId) {
        return (root, query, criteriaBuilder) -> 
            userPublicId == null ? null : criteriaBuilder.equal(root.get("userPublicId"), userPublicId);
    }

    public static Specification<Notification> hasStatus(NotificationStatus status) {
        return (root, query, criteriaBuilder) -> 
            status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Notification> hasType(NotificationType type) {
        return (root, query, criteriaBuilder) -> 
            type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }

    public static Specification<Notification> createdAfter(LocalDateTime fromDate) {
        return (root, query, criteriaBuilder) -> 
            fromDate == null ? null : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
    }

    public static Specification<Notification> createdBefore(LocalDateTime toDate) {
        return (root, query, criteriaBuilder) -> 
            toDate == null ? null : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), toDate);
    }

    public static Specification<Notification> isUnread() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNull(root.get("readAt"));
    }

    public static Specification<Notification> isRead() {
        return (root, query, criteriaBuilder) -> 
            criteriaBuilder.isNotNull(root.get("readAt"));
    }

    public static Specification<Notification> titleContains(String titleContains) {
        return (root, query, criteriaBuilder) -> 
            titleContains == null || titleContains.trim().isEmpty() ? null : 
            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), 
                "%" + titleContains.toLowerCase() + "%");
    }

    public static Specification<Notification> contentContains(String contentContains) {
        return (root, query, criteriaBuilder) -> 
            contentContains == null || contentContains.trim().isEmpty() ? null : 
            criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), 
                "%" + contentContains.toLowerCase() + "%");
    }
}