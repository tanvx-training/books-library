package com.library.member.repository;

public enum LibraryCardStatus {

    ACTIVE,

    INACTIVE,

    EXPIRED,

    LOST,

    BLOCKED;

    public boolean canUseLibraryServices() {
        return this == ACTIVE;
    }

    public boolean canBeReactivated() {
        return this == INACTIVE || this == EXPIRED;
    }

    public boolean requiresReplacement() {
        return this == LOST;
    }

    public boolean hasIssues() {
        return this == LOST || this == BLOCKED || this == EXPIRED;
    }

    public boolean canBeRenewed() {
        return this == ACTIVE || this == EXPIRED;
    }
}