package com.library.notification.controller;

import com.library.notification.service.NotificationPreferencesService;
import com.library.notification.dto.request.UpdatePreferencesRequest;
import com.library.notification.dto.response.NotificationPreferencesResponse;
import com.library.notification.service.UnifiedAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notification-preferences")
@RequiredArgsConstructor
@Slf4j
public class NotificationPreferencesController {

    private final NotificationPreferencesService preferencesService;
    private final UnifiedAuthenticationService authenticationService;

    @GetMapping
    public ResponseEntity<NotificationPreferencesResponse> getPreferences() {
        UUID userPublicId = authenticationService.getCurrentUserPublicId();
        if (userPublicId == null) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Getting notification preferences for user: {}", userPublicId);

        NotificationPreferencesResponse response = preferencesService.getPreferences(userPublicId);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<NotificationPreferencesResponse> updatePreferences(
            @Valid @RequestBody UpdatePreferencesRequest request) {
        
        UUID userPublicId = authenticationService.getCurrentUserPublicId();
        if (userPublicId == null) {
            return ResponseEntity.badRequest().build();
        }

        log.debug("Updating notification preferences for user: {}", userPublicId);

        NotificationPreferencesResponse response = preferencesService.updatePreferences(userPublicId, request);
        return ResponseEntity.ok(response);
    }
}