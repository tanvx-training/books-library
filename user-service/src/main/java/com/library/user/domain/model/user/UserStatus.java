package com.library.user.domain.model.user;

/**
 * User status enumeration
 */
public enum UserStatus {
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    SUSPENDED("Suspended"),
    BANNED("Banned"),
    PENDING_VERIFICATION("Pending Verification");
    
    private final String displayName;
    
    UserStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public boolean isActive() {
        return this == ACTIVE;
    }
    
    public boolean canBorrowBooks() {
        return this == ACTIVE;
    }
    
    public boolean canAccessLibrary() {
        return this == ACTIVE || this == INACTIVE;
    }
}