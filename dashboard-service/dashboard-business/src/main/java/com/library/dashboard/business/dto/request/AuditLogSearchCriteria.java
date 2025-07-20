package com.library.dashboard.business.dto.request;

import com.library.dashboard.repository.enums.ActionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuditLogSearchCriteria {

    private String serviceName;
    private String entityName;
    private String entityId;
    private ActionType actionType;
    private String userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public boolean hasAnyCriteria() {
        return serviceName != null || entityName != null || entityId != null || 
               actionType != null || userId != null || startDate != null || endDate != null;
    }

    public boolean hasDateRange() {
        return startDate != null || endDate != null;
    }
}