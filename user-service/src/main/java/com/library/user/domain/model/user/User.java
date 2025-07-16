package com.library.user.domain.model.user;

import com.library.user.domain.event.UserCreatedEvent;
import com.library.user.domain.event.UserUpdatedEvent;
import com.library.user.domain.exception.InvalidUserDataException;
import com.library.user.domain.model.shared.AggregateRoot;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class User extends AggregateRoot {
    private UserId id;
    private KeycloakId keycloakId;
    private Username username;
    private Email email;
    private PasswordHash password;
    private FirstName firstName;
    private LastName lastName;
    private Phone phone;
    private Set<Role> roles;
    private boolean active;

    // Package-private constructor for factory methods and mappers
    User() {}
    
    // Package-private setters for mappers
    void setId(UserId id) {
        this.id = id;
    }
    
    // This method is used by both the mapper and the public API
    public void setKeycloakId(KeycloakId keycloakId) {
        this.keycloakId = keycloakId;
        registerEvent(new UserUpdatedEvent(this));
    }
    
    public void setUsername(Username username) {
        this.username = username;
        registerEvent(new UserUpdatedEvent(this));
    }
    
    public void setEmail(Email email) {
        this.email = email;
        registerEvent(new UserUpdatedEvent(this));
    }
    
    public void setPassword(PasswordHash password) {
        this.password = password;
        registerEvent(new UserUpdatedEvent(this));
    }
    
    public void setFirstName(FirstName firstName) {
        this.firstName = firstName;
        registerEvent(new UserUpdatedEvent(this));
    }
    
    public void setLastName(LastName lastName) {
        this.lastName = lastName;
        registerEvent(new UserUpdatedEvent(this));
    }
    
    public void setPhone(Phone phone) {
        this.phone = phone;
        registerEvent(new UserUpdatedEvent(this));
    }
    
    void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    
    void setActive(boolean active) {
        this.active = active;
    }

    // Factory method for creating a new user
    public static User create(
            Username username,
            Email email,
            PasswordHash password,
            FirstName firstName,
            LastName lastName,
            Phone phone,
            Set<Role> roles) {

        User user = new User();
        user.id = UserId.createNew();
        user.username = username;
        user.email = email;
        user.password = password;
        user.firstName = firstName;
        user.lastName = lastName;
        user.phone = phone;
        user.roles = roles != null ? roles : new HashSet<>();
        user.active = true;

        user.registerEvent(new UserCreatedEvent(user));

        return user;
    }

    // Factory method for creating a user with Keycloak ID
    public static User createWithKeycloak(
            KeycloakId keycloakId,
            Username username,
            Email email,
            FirstName firstName,
            LastName lastName,
            Phone phone) {

        User user = new User();
        user.id = UserId.createNew();
        user.keycloakId = keycloakId;
        user.username = username;
        user.email = email;
        user.password = null; // Password is managed by Keycloak
        user.firstName = firstName;
        user.lastName = lastName;
        user.phone = phone;
        user.roles = new HashSet<>(); // Roles are managed by Keycloak
        user.active = true;

        user.registerEvent(new UserCreatedEvent(user));

        return user;
    }

    // Method to update user information
    public void updateInformation(
            FirstName firstName,
            LastName lastName,
            Phone phone) {

        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;

        registerEvent(new UserUpdatedEvent(this));
    }
    
    // Method to update email with validation
    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new InvalidUserDataException("Email cannot be null");
        }
        this.email = newEmail;
        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to update password
    public void updatePassword(PasswordHash newPassword) {
        if (newPassword == null) {
            throw new InvalidUserDataException("Password cannot be null");
        }
        this.password = newPassword;
        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to add a role
    public void addRole(Role role) {
        if (role == null) {
            throw new InvalidUserDataException("Role cannot be null");
        }
        this.roles.add(role);
        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to remove a role
    public void removeRole(Role role) {
        if (role == null) {
            throw new InvalidUserDataException("Role cannot be null");
        }
        this.roles.remove(role);
        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to deactivate user
    public void deactivate() {
        this.active = false;
        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to activate user
    public void activate() {
        this.active = true;
        registerEvent(new UserUpdatedEvent(this));
    }

    // For JPA/ORM reconstruction
    public static User reconstitute(
            UserId id,
            Username username,
            Email email,
            PasswordHash password,
            FirstName firstName,
            LastName lastName,
            Phone phone,
            Set<Role> roles,
            boolean active) {

        User user = new User();
        user.id = id;
        user.username = username;
        user.email = email;
        user.password = password;
        user.firstName = firstName;
        user.lastName = lastName;
        user.phone = phone;
        user.roles = roles;
        user.active = active;

        return user;
    }
}