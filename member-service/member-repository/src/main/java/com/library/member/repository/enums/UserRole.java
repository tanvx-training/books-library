package com.library.member.repository.enums;

public enum UserRole {

    MEMBER,
    LIBRARIAN,
    ADMIN;

    public boolean isAdmin() {
        return this == ADMIN;
    }
    public boolean isLibrarian() {
        return this == LIBRARIAN || this == ADMIN;
    }
    public boolean canAccessMemberProfiles() {
        return this == LIBRARIAN || this == ADMIN;
    }
    public boolean canModifyMemberProfiles() {
        return this == ADMIN;
    }
}