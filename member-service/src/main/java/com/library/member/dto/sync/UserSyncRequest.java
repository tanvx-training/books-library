package com.library.member.dto.sync;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class UserSyncRequest {

    @NotBlank(message = "Keycloak ID is required")
    private String keycloakId;

    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String phoneNumber;

    private String address;

    private LocalDate dateOfBirth;

    private Set<String> roles;

    private Boolean isActive;
}