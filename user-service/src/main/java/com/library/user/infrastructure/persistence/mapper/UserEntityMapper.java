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
        // Create a base user object
        User user = new User();

        // Set ID
        if (jpaEntity.getId() != null) {
            user.setId(UserId.of(jpaEntity.getId()));
        }

        // Set keycloakId if available
        if (jpaEntity.getKeycloakId() != null) {
            user.setKeycloakId(KeycloakId.of(jpaEntity.getKeycloakId()));
        }

        // Set other properties
        user.setUsername(new Username(jpaEntity.getUsername()));
        user.setEmail(new Email(jpaEntity.getEmail()));
        
        // Password may be null for Keycloak users
        if (jpaEntity.getPassword() != null) {
            user.setPassword(new PasswordHash(jpaEntity.getPassword()));
        }

        if (jpaEntity.getFirstName() != null) {
            user.setFirstName(new FirstName(jpaEntity.getFirstName()));
        }

        if (jpaEntity.getLastName() != null) {
            user.setLastName(new LastName(jpaEntity.getLastName()));
        }

        if (jpaEntity.getPhone() != null) {
            user.setPhone(new Phone(jpaEntity.getPhone()));
        }

        user.setActive(!jpaEntity.isDeleteFlg());

        // Convert roles
        Set<Role> roles = jpaEntity.getRoles().stream()
                .map(roleEntity -> new Role(roleEntity.getId(), roleEntity.getName()))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return user;
    }
}