package com.library.user.domain.factory;

import com.library.user.domain.exception.InvalidUserDataException;
import com.library.user.domain.exception.UserNotFoundException;
import com.library.user.domain.model.user.*;
import com.library.user.domain.repository.UserRepository;
import com.library.user.domain.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Enhanced factory for creating User aggregates with complex business rules
 */
@Component
@RequiredArgsConstructor
public class EnhancedUserFactory {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSpecification eligibilitySpecification;
    
    /**
     * Create a standard user with validation
     */
    public User createStandardUser(UserCreationRequest request) {
        validateUserCreationRequest(request);
        
        // Create value objects
        Username username = Username.of(request.getUsername());
        Email email = Email.of(request.getEmail());
        PasswordHash password = PasswordHash.of(passwordEncoder.encode(request.getPassword()));
        
        // Validate uniqueness
        validateUniqueness(username, email);
        
        // Create user with default role
        Set<Role> defaultRoles = Set.of(Role.of("USER"));
        
        User user = User.create(
            username, email, password,
            FirstName.of(request.getFirstName()),
            LastName.of(request.getLastName()),
            request.getPhone() != null ? Phone.of(request.getPhone()) : null,
            defaultRoles
        );
        
        // Apply business rules validation
        if (!eligibilitySpecification.isSatisfiedBy(user)) {
            throw new InvalidUserDataException("user", "User does not meet creation requirements");
        }
        
        return user;
    }
    
    /**
     * Create a Keycloak-integrated user
     */
    public User createKeycloakUser(KeycloakUserInfo keycloakInfo) {
        validateKeycloakUserInfo(keycloakInfo);
        
        // Check if user already exists
        if (userRepository.existsByKeycloakId(KeycloakId.of(keycloakInfo.getId()))) {
            throw new InvalidUserDataException("keycloakId", "User with Keycloak ID already exists");
        }
        
        return User.createWithKeycloak(
            KeycloakId.of(keycloakInfo.getId()),
            Username.of(keycloakInfo.getUsername()),
            Email.of(keycloakInfo.getEmail()),
            FirstName.of(keycloakInfo.getFirstName()),
            LastName.of(keycloakInfo.getLastName()),
            keycloakInfo.getPhone() != null ? Phone.of(keycloakInfo.getPhone()) : null
        );
    }
    
    /**
     * Create a user with complete profile information
     */
    public User createUserWithProfile(UserCreationRequest request, PersonalInfo personalInfo, 
                                    ContactInfo contactInfo) {
        // Validate age requirement
        if (personalInfo.getAge() < 13) {
            throw new InvalidUserDataException("age", "User must be at least 13 years old");
        }
        
        User user = createStandardUser(request);
        
        // Additional validations based on profile
        validateProfileConsistency(user, personalInfo, contactInfo);
        
        return user;
    }
    
    /**
     * Create a librarian user with elevated privileges
     */
    public User createLibrarianUser(UserCreationRequest request, String librarianCode) {
        validateLibrarianCode(librarianCode);
        
        User user = createStandardUser(request);
        
        // Add librarian role
        user.addRole(Role.of("LIBRARIAN"));
        
        return user;
    }
    
    private void validateUserCreationRequest(UserCreationRequest request) {
        if (request == null) {
            throw new InvalidUserDataException("request", "User creation request cannot be null");
        }
        
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new InvalidUserDataException("username", "Username cannot be empty");
        }
        
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new InvalidUserDataException("email", "Email cannot be empty");
        }
        
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new InvalidUserDataException("password", "Password must be at least 8 characters");
        }
        
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new InvalidUserDataException("firstName", "First name cannot be empty");
        }
        
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new InvalidUserDataException("lastName", "Last name cannot be empty");
        }
    }
    
    private void validateKeycloakUserInfo(KeycloakUserInfo keycloakInfo) {
        if (keycloakInfo == null) {
            throw new InvalidUserDataException("keycloakInfo", "Keycloak user info cannot be null");
        }
        
        if (keycloakInfo.getId() == null || keycloakInfo.getId().trim().isEmpty()) {
            throw new InvalidUserDataException("keycloakId", "Keycloak ID cannot be empty");
        }
        
        if (keycloakInfo.getUsername() == null || keycloakInfo.getUsername().trim().isEmpty()) {
            throw new InvalidUserDataException("username", "Username cannot be empty");
        }
        
        if (keycloakInfo.getEmail() == null || keycloakInfo.getEmail().trim().isEmpty()) {
            throw new InvalidUserDataException("email", "Email cannot be empty");
        }
    }
    
    private void validateUniqueness(Username username, Email email) {
        if (userRepository.existsByUsername(username)) {
            throw new InvalidUserDataException("username", "Username already exists: " + username.getValue());
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new InvalidUserDataException("email", "Email already exists: " + email.getValue());
        }
    }
    
    private void validateProfileConsistency(User user, PersonalInfo personalInfo, ContactInfo contactInfo) {
        // Validate that email matches
        if (!user.getEmail().equals(contactInfo.getPrimaryEmail())) {
            throw new InvalidUserDataException("email", "User email and contact email must match");
        }
        
        // Validate that names match
        if (!user.getFirstName().equals(personalInfo.getFirstName()) ||
            !user.getLastName().equals(personalInfo.getLastName())) {
            throw new InvalidUserDataException("name", "User name and personal info name must match");
        }
    }
    
    private void validateLibrarianCode(String librarianCode) {
        // Business rule: Librarian code must follow specific format
        if (librarianCode == null || !librarianCode.matches("LIB\\d{6}")) {
            throw new InvalidUserDataException("librarianCode", "Invalid librarian code format");
        }
        
        // Additional validation: Check if code is already used
        // This would typically involve checking against a librarian registry
    }
    
    /**
     * Request object for user creation
     */
    public static class UserCreationRequest {
        private String username;
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;
        
        // Constructors, getters, setters
        public UserCreationRequest(String username, String email, String password, 
                                 String firstName, String lastName, String phone) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
        }
        
        // Getters
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getPassword() { return password; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhone() { return phone; }
    }
    
    /**
     * Keycloak user information
     */
    public static class KeycloakUserInfo {
        private String id;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        
        public KeycloakUserInfo(String id, String username, String email, 
                              String firstName, String lastName, String phone) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
        }
        
        // Getters
        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
        public String getFirstName() { return firstName; }
        public String getLastName() { return lastName; }
        public String getPhone() { return phone; }
    }
}