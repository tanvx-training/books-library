package com.library.common.enums;

/**
 * Enum defining different logging levels with their characteristics
 */
public enum LogLevel {
    
    BASIC("BASIC", "Basic information logging", true, false, false),
    DETAILED("DETAILED", "Detailed information logging", true, true, false),
    ADVANCED("ADVANCED", "Advanced information logging with tracing", true, true, true);
    
    private final String code;
    private final String description;
    private final boolean includeBasicInfo;
    private final boolean includeDetailedInfo;
    private final boolean includeAdvancedInfo;
    
    LogLevel(String code, String description, boolean includeBasicInfo, 
             boolean includeDetailedInfo, boolean includeAdvancedInfo) {
        this.code = code;
        this.description = description;
        this.includeBasicInfo = includeBasicInfo;
        this.includeDetailedInfo = includeDetailedInfo;
        this.includeAdvancedInfo = includeAdvancedInfo;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public boolean includeBasicInfo() {
        return includeBasicInfo;
    }
    
    public boolean includeDetailedInfo() {
        return includeDetailedInfo;
    }
    
    public boolean includeAdvancedInfo() {
        return includeAdvancedInfo;
    }
} 