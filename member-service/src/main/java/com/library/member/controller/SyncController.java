package com.library.member.controller;

import com.library.member.service.KeycloakSyncService;
import com.library.member.dto.sync.SyncResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/sync")
@RequiredArgsConstructor
@Slf4j
public class SyncController {

    private final KeycloakSyncService syncService;

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SyncResult> syncAllUsers() {
        log.info("Manual user sync triggered");
        
        try {
            SyncResult result = syncService.syncAllUsers();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Manual user sync failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/users/{keycloakId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> syncSingleUser(@PathVariable String keycloakId) {
        log.info("Manual sync triggered for user: {}", keycloakId);
        
        try {
            syncService.syncUserById(keycloakId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Manual sync failed for user: {}", keycloakId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}