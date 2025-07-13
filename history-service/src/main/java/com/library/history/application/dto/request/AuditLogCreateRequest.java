package com.library.history.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogCreateRequest {
    
    @NotBlank(message = "Service name is required")
    @Size(max = 100, message = "Service name cannot exceed 100 characters")
    private String serviceName;
    
    @NotBlank(message = "Entity name is required")
    @Size(max = 100, message = "Entity name cannot exceed 100 characters")
    private String entityName;
    
    @NotBlank(message = "Entity ID is required")
    @Size(max = 255, message = "Entity ID cannot exceed 255 characters")
    private String entityId;
    
    @NotBlank(message = "Action type is required")
    private String actionType;
    
    private String userId;
    
    private String userInfo;
    
    private String oldValue;
    
    private String newValue;
    
    private String changes;
    
    private String requestId;
}