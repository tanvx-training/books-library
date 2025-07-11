package com.library.user.domain.factory;

import com.library.user.domain.model.user.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            String phone,
            Set<String> roleNames) {

        // Create value objects
        Username usernameVO = Username.of(username);
        Email emailVO = Email.of(email);
        PasswordHash passwordHash = PasswordHash.of(passwordEncoder.encode(password));
        FirstName firstNameVO = firstName != null ? FirstName.of(firstName) : FirstName.of(null);
        LastName lastNameVO = lastName != null ? LastName.of(lastName) : LastName.of(null);
        Phone phoneVO = phone != null ? Phone.of(phone) : Phone.of(null);

        // Convert role names to Role objects
        Set<Role> roles = new HashSet<>();
        if (roleNames != null) {
            for (String roleName : roleNames) {
                roles.add(Role.of(roleName));
            }
        }

        // Create user
        return User.create(
                usernameVO,
                emailVO,
                passwordHash,
                firstNameVO,
                lastNameVO,
                phoneVO,
                roles
        );
    }

    public User createAdminUser(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            String phone) {

        Set<String> roleNames = new HashSet<>();
        roleNames.add("ROLE_ADMIN");
        roleNames.add("ROLE_USER");

        return createUser(username, email, password, firstName, lastName, phone, roleNames);
    }

    public User createStandardUser(
            String username,
            String email,
            String password,
            String firstName,
            String lastName,
            String phone) {

        Set<String> roleNames = new HashSet<>();
        roleNames.add("ROLE_USER");

        return createUser(username, email, password, firstName, lastName, phone, roleNames);
    }
}