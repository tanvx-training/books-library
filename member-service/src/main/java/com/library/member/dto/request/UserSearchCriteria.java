package com.library.member.dto.request;

import com.library.member.repository.UserRole;
import lombok.Builder;
import lombok.Data;

/**
 * Search criteria for filtering users.
 * Used for admin user listing and search functionality.
 */
@Data
@Builder
public class UserSearchCriteria {
    
    /**
     * Search term for name or email.
     */
    private String searchTerm;
    
    /**
     * Filter by user role.
     */
    private UserRole role;
    
    /**
     * Filter by active status.
     */
    private Boolean isActive;
    
    /**
     * Page number (0-based).
     */
    @Builder.Default
    private Integer page = 0;
    
    /**
     * Page size.
     */
    @Builder.Default
    private Integer size = 20;
    
    /**
     * Sort field.
     */
    @Builder.Default
    private String sortBy = "createdAt";
    
    /**
     * Sort direction (asc/desc).
     */
    @Builder.Default
    private String sortDirection = "desc";
}