package com.library.user.infrastructure.enums;

import lombok.Getter;

/**
 * Enum defining different types of operations for categorizing logs
 */
@Getter
public enum OperationType {
    
    CREATE("CREATE", "Resource creation operation"),
    READ("READ", "Resource read operation"),
    UPDATE("UPDATE", "Resource update operation"),
    DELETE("DELETE", "Resource deletion operation"),
    SEARCH("SEARCH", "Resource search operation"),
    AUTHENTICATION("AUTH", "Authentication operation"),
    AUTHORIZATION("AUTHZ", "Authorization operation"),
    VALIDATION("VALIDATION", "Data validation operation"),
    BUSINESS_LOGIC("BUSINESS", "Business logic operation"),
    DATABASE("DATABASE", "Database operation"),
    EXTERNAL_API("EXTERNAL_API", "External API call"),
    FILE_OPERATION("FILE_OP", "File operation"),
    CACHE("CACHE", "Cache operation"),
    NOTIFICATION("NOTIFICATION", "Notification operation"),
    OTHER("OTHER", "Other operation");
    
    private final String code;
    private final String description;
    
    OperationType(String code, String description) {
        this.code = code;
        this.description = description;
    }

} 