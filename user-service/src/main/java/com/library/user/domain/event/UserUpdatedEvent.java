package com.library.user.domain.event;

import com.library.user.domain.model.shared.DomainEvent;
import com.library.user.domain.model.user.Role;
import com.library.user.domain.model.user.User;
import lombok.Getter;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserUpdatedEvent extends DomainEvent {
    private final Long id;
    private final String username;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phone;
    private final boolean active;
    private final Set<String> roles;

    public UserUpdatedEvent(User user) {
        this.id = user.getId() != null ? user.getId().getValue() : null;
        this.username = user.getUsername().getValue();
        this.email = user.getEmail().getValue();
        this.firstName = user.getFirstName() != null ? user.getFirstName().getValue() : null;
        this.lastName = user.getLastName() != null ? user.getLastName().getValue() : null;
        this.phone = user.getPhone() != null ? user.getPhone().getValue() : null;
        this.active = user.isActive();
        this.roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }
}