package com.library.history.interfaces.rest;

import com.library.history.application.dto.request.AuditLogCreateRequest;
import com.library.history.application.dto.response.AuditLogResponse;
import com.library.history.application.service.AuditLogApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogApplicationService auditLogApplicationService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLogResponse> createAuditLog(@Valid @RequestBody AuditLogCreateRequest request) {
        log.info("REST request to create audit log for entity: {}, id: {}", request.getEntityName(), request.getEntityId());
        AuditLogResponse response = auditLogApplicationService.createAuditLog(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuditLogResponse> getAuditLogById(@PathVariable UUID id) {
        log.info("REST request to get audit log with ID: {}", id);
        AuditLogResponse response = auditLogApplicationService.getAuditLogById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/entity/{entityName}/{entityId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEntityNameAndEntityId(
            @PathVariable String entityName,
            @PathVariable String entityId) {
        log.info("REST request to get audit logs for entity: {}, id: {}", entityName, entityId);
        List<AuditLogResponse> response = auditLogApplicationService.getAuditLogsByEntityNameAndEntityId(entityName, entityId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByUserId(@PathVariable String userId) {
        log.info("REST request to get audit logs for user ID: {}", userId);
        List<AuditLogResponse> response = auditLogApplicationService.getAuditLogsByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/time-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAuditLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        log.info("REST request to get audit logs between {} and {}", start, end);
        List<AuditLogResponse> response = auditLogApplicationService.getAuditLogsByTimeRange(start, end);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuditLog(@PathVariable UUID id) {
        log.info("REST request to delete audit log with ID: {}", id);
        auditLogApplicationService.deleteAuditLog(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
        log.info("REST request to get all audit logs");
        List<AuditLogResponse> response = auditLogApplicationService.getAllAuditLogs();
        return ResponseEntity.ok(response);
    }
} 