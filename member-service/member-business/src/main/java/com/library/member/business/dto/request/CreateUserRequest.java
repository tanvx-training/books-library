package com.library.member.business.dto.request;

import com.library.member.repository.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for creating a new user.
 * Used by administrators to create user accounts.
 */
@Data
@Builder
public class CreateUserRequest {
    
    /**
     * The user's Keycloak identifier.
     */
    @NotBlank(message = "Keycloak ID is required")
    private String keycloakId;
    
    /**
     * The user's username.
     */
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    /**
     * The user's email address.
     */
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    /**
     * The user's first name.
     */
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;
    
    /**
     * The user's last name.
     */
    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    private String lastName;
    
    /**
     * The user's phone number.
     */
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    /**
     * The user's address.
     */
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    /**
     * The user's date of birth.
     */
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;
    
    /**
     * The user's role in the system.
     */
    @NotNull(message = "User role is required")
    private UserRole role;
}