package com.library.notification.dto.request;

import com.library.notification.repository.NotificationType;
import com.library.notification.repository.NotificationStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class NotificationSearchRequest {

    private UUID userPublicId;
    private NotificationType type;
    private NotificationStatus status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String titleContains;
    private String contentContains;
    private Boolean isRead;
    
    // Pagination parameters
    private int page = 0;
    private int size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}