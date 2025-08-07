package com.library.notification.controller.rest;

import com.library.notification.business.NotificationService;
import com.library.notification.business.dto.request.NotificationSearchRequest;
import com.library.notification.business.dto.response.NotificationResponse;
import com.library.notification.business.dto.response.PagedNotificationResponse;
import com.library.notification.business.security.UnifiedAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final UnifiedAuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<PagedNotificationResponse> getUserNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        UUID userPublicId = authenticationService.getCurrentUserPublicId();
        if (userPublicId == null) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Getting notifications for user: {} with page: {}, size: {}", userPublicId, page, size);

        PagedNotificationResponse response = notificationService.getUserNotifications(
                userPublicId, page, size, sortBy, sortDirection);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable UUID id) {
        UUID userPublicId = authenticationService.getCurrentUserPublicId();
        if (userPublicId == null) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Getting notification {} for user: {}", id, userPublicId);

        NotificationResponse response = notificationService.getNotificationById(id, userPublicId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable UUID id) {
        UUID userPublicId = authenticationService.getCurrentUserPublicId();
        if (userPublicId == null) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Marking notification {} as read for user: {}", id, userPublicId);

        NotificationResponse response = notificationService.markAsRead(id, userPublicId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedNotificationResponse> searchNotifications(@ModelAttribute NotificationSearchRequest searchRequest) {

        UUID userPublicId = authenticationService.getCurrentUserPublicId();
        if (userPublicId == null) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Searching notifications for user: {} with filters", userPublicId);
        PagedNotificationResponse response = notificationService.searchNotifications(searchRequest);
        return ResponseEntity.ok(response);
    }
}