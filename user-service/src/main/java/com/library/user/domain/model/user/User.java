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
    private Username username;
    private Email email;
    private PasswordHash password;
    private FirstName firstName;
    private LastName lastName;
    private Phone phone;
    private Set<Role> roles;
    private boolean active;

    // Private constructor for factory methods
    private User() {}

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

    // Method to update password
    public void updatePassword(PasswordHash newPassword) {
        if (newPassword == null) {
            throw new InvalidUserDataException("password", "Password không được để trống");
        }

        this.password = newPassword;

        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to update email
    public void updateEmail(Email newEmail) {
        if (newEmail == null) {
            throw new InvalidUserDataException("email", "Email không được để trống");
        }

        this.email = newEmail;

        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to add role
    public void addRole(Role role) {
        if (role == null) {
            throw new InvalidUserDataException("role", "Role không được để trống");
        }

        this.roles.add(role);

        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to remove role
    public void removeRole(Role role) {
        if (role == null) {
            throw new InvalidUserDataException("role", "Role không được để trống");
        }

        this.roles.remove(role);

        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to activate user
    public void activate() {
        this.active = true;

        registerEvent(new UserUpdatedEvent(this));
    }

    // Method to deactivate user
    public void deactivate() {
        this.active = false;

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