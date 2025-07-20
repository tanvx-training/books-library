package com.library.dashboard.business.dto.request;

import com.library.dashboard.repository.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogSearchRequest {

    @Size(max = 100, message = "Service name must not exceed 100 characters")
    private String serviceName;

    @Size(max = 100, message = "Entity name must not exceed 100 characters")
    private String entityName;

    @Size(max = 255, message = "Entity ID must not exceed 255 characters")
    private String entityId;

    private ActionType actionType;

    @Size(max = 36, message = "User ID must not exceed 36 characters")
    private String userId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime endDate;
}