package com.library.member.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * Response DTO for user information.
 * Contains user profile data that can be safely exposed to clients.
 */
@Data
@Builder
public class UserResponse {
    
    /**
     * The user's public UUID identifier.
     */
    private UUID publicId;
    
    /**
     * The user's Keycloak identifier.
     */
    private String keycloakId;
    
    /**
     * The user's username.
     */
    private String username;
    
    /**
     * The user's email address.
     */
    private String email;
    
    /**
     * The user's first name.
     */
    private String firstName;
    
    /**
     * The user's last name.
     */
    private String lastName;
    
    /**
     * The user's phone number.
     */
    private String phoneNumber;
    
    /**
     * The user's address.
     */
    private String address;
    
    /**
     * The user's date of birth.
     */
    private LocalDate dateOfBirth;
    
    /**
     * The user's roles in the system.
     */
    private Set<String> roles;
    
    /**
     * Whether the user account is active.
     */
    private Boolean isActive;
    
    /**
     * When the user record was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * When the user record was last updated.
     */
    private LocalDateTime updatedAt;
    
    /**
     * Gets the user's full name.
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username != null ? username : keycloakId;
        }
    }
}