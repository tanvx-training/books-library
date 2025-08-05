package com.library.member.business.dto.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * Request DTO for updating user profile information.
 * Contains fields that users can update about their own profile.
 */
@Data
@Builder
public class UpdateUserRequest {
    
    /**
     * The user's first name.
     */
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    private String firstName;
    
    /**
     * The user's last name.
     */
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
}