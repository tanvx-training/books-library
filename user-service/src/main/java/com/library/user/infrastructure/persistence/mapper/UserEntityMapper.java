package com.library.user.infrastructure.persistence.mapper;

import com.library.user.domain.model.user.*;
import com.library.user.infrastructure.persistence.entity.RoleJpaEntity;
import com.library.user.infrastructure.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserEntityMapper {

    /**
     * Convert domain entity to JPA entity
     */
    public UserJpaEntity toJpaEntity(User user) {
        UserJpaEntity jpaEntity = new UserJpaEntity();

        // Set ID if not null (for updates)
        if (user.getId() != null && user.getId().getValue() != null) {
            jpaEntity.setId(user.getId().getValue());
        }

        // Set keycloakId if available
        if (user.getKeycloakId() != null && user.getKeycloakId().getValue() != null) {
            jpaEntity.setKeycloakId(user.getKeycloakId().getValue());
        }

        // Set other properties
        jpaEntity.setUsername(user.getUsername().getValue());
        jpaEntity.setEmail(user.getEmail().getValue());
        
        // Password may be null for Keycloak users
        if (user.getPassword() != null && user.getPassword().getValue() != null) {
            jpaEntity.setPassword(user.getPassword().getValue());
        }

        if (user.getFirstName() != null && user.getFirstName().getValue() != null) {
            jpaEntity.setFirstName(user.getFirstName().getValue());
        }

        if (user.getLastName() != null && user.getLastName().getValue() != null) {
            jpaEntity.setLastName(user.getLastName().getValue());
        }

        if (user.getPhone() != null && user.getPhone().getValue() != null) {
            jpaEntity.setPhone(user.getPhone().getValue());
        }

        jpaEntity.setDeleteFlg(!user.isActive());

        // Convert roles
        Set<RoleJpaEntity> roleEntities = new HashSet<>();
        for (Role role : user.getRoles()) {
            RoleJpaEntity roleEntity = new RoleJpaEntity();
            roleEntity.setId(role.getId());
            roleEntity.setName(role.getName());
            roleEntities.add(roleEntity);
        }
        jpaEntity.setRoles(roleEntities);

        return jpaEntity;
    }

    /**
     * Convert JPA entity to domain entity
     */
    public User toDomainEntity(UserJpaEntity jpaEntity) {
        // Use the reconstitute method to create domain entity from persistence data
        UserId userId = jpaEntity.getId() != null ? UserId.of(jpaEntity.getId()) : null;
        Username username = new Username(jpaEntity.getUsername());
        Email email = new Email(jpaEntity.getEmail());
        
        // Password may be null for Keycloak users
        PasswordHash password = jpaEntity.getPassword() != null ? 
            new PasswordHash(jpaEntity.getPassword()) : null;

        FirstName firstName = jpaEntity.getFirstName() != null ? 
            new FirstName(jpaEntity.getFirstName()) : null;
        
        LastName lastName = jpaEntity.getLastName() != null ? 
            new LastName(jpaEntity.getLastName()) : null;

        Phone phone = jpaEntity.getPhone() != null ? 
            new Phone(jpaEntity.getPhone()) : null;

        // Convert roles
        Set<Role> roles = jpaEntity.getRoles().stream()
                .map(roleEntity -> new Role(roleEntity.getId(), roleEntity.getName()))
                .collect(Collectors.toSet());

        boolean active = !jpaEntity.isDeleteFlg();

        // Use reconstitute method to create the user
        User user = User.reconstitute(
            userId, username, email, password, 
            firstName, lastName, phone, roles, active
        );

        // Set keycloakId if available (this is a public method)
        if (jpaEntity.getKeycloakId() != null) {
            user.setKeycloakId(KeycloakId.of(jpaEntity.getKeycloakId()));
        }

        return user;
    }
}