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

        // Set other properties
        jpaEntity.setUsername(user.getUsername().getValue());
        jpaEntity.setEmail(user.getEmail().getValue());
        jpaEntity.setPassword(user.getPassword().getValue());

        if (user.getFirstName() != null && user.getFirstName().getValue() != null) {
            jpaEntity.setFirstName(user.getFirstName().getValue());
        }

        if (user.getLastName() != null && user.getLastName().getValue() != null) {
            jpaEntity.setLastName(user.getLastName().getValue());
        }

        if (user.getPhone() != null && user.getPhone().getValue() != null) {
            jpaEntity.setPhone(user.getPhone().getValue());
        }

        jpaEntity.setActive(user.isActive());

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
        // Create value objects
        UserId id = UserId.of(jpaEntity.getId());
        Username username = Username.of(jpaEntity.getUsername());
        Email email = Email.of(jpaEntity.getEmail());
        PasswordHash password = PasswordHash.of(jpaEntity.getPassword());

        // Optional value objects
        FirstName firstName = jpaEntity.getFirstName() != null && !jpaEntity.getFirstName().isEmpty()
                ? FirstName.of(jpaEntity.getFirstName())
                : FirstName.of(null);

        LastName lastName = jpaEntity.getLastName() != null && !jpaEntity.getLastName().isEmpty()
                ? LastName.of(jpaEntity.getLastName())
                : LastName.of(null);

        Phone phone = jpaEntity.getPhone() != null && !jpaEntity.getPhone().isEmpty()
                ? Phone.of(jpaEntity.getPhone())
                : Phone.of(null);

        // Convert roles
        Set<Role> roles = jpaEntity.getRoles().stream()
                .map(roleEntity -> Role.of(roleEntity.getId(), roleEntity.getName()))
                .collect(Collectors.toSet());

        // Reconstitute the domain entity
        return User.reconstitute(
                id,
                username,
                email,
                password,
                firstName,
                lastName,
                phone,
                roles,
                jpaEntity.isActive()
        );
    }
}