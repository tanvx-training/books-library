package com.library.notification.business.dto.response;

import lombok.Data;

import java.util.List;

/**
 * Response DTO for paginated notification data
 */
@Data
public class PagedNotificationResponse {

    private List<NotificationResponse> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
}