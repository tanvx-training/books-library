package com.library.dashboard.controller;

import com.library.dashboard.service.AuditLogService;
import com.library.dashboard.service.AuditLogMapper;
import com.library.dashboard.dto.request.AuditLogSearchRequest;
import com.library.dashboard.dto.request.AuditLogSearchCriteria;
import com.library.dashboard.dto.response.AuditLogResponse;
import com.library.dashboard.dto.response.PagedAuditLogResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/audit-logs")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final AuditLogMapper auditLogMapper;

    @GetMapping
    public ResponseEntity<PagedAuditLogResponse> findAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        PagedAuditLogResponse response = auditLogService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<PagedAuditLogResponse> search(
            @Valid AuditLogSearchRequest searchRequest,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "timestamp") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            HttpServletRequest request) {

        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        AuditLogSearchCriteria criteria = auditLogMapper.toCriteria(searchRequest);
        PagedAuditLogResponse response = auditLogService.search(criteria, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditLogResponse> findById(@PathVariable UUID id, HttpServletRequest request) {

        AuditLogResponse auditLog = auditLogService.findById(id);
        return ResponseEntity.ok(auditLog);
    }
}