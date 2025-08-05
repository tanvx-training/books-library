package com.library.member.business.mapper;

import com.library.member.business.dto.request.CreateUserRequest;
import com.library.member.business.dto.request.UpdateUserRequest;
import com.library.member.business.dto.response.UserResponse;
import com.library.member.business.dto.sync.UserSyncRequest;
import com.library.member.business.security.AuthenticatedUser;
import com.library.member.repository.entity.UserEntity;
import com.library.member.repository.enums.UserRole;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class UserMapper {

    public UserResponse toUserResponse(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return UserResponse.builder()
                .publicId(entity.getPublicId())
                .keycloakId(entity.getKeycloakId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .phoneNumber(entity.getPhoneNumber())
                .address(entity.getAddress())
                .dateOfBirth(entity.getDateOfBirth())
                .roles(mapRoleToSet(entity.getRole()))
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserResponse toUserResponse(AuthenticatedUser authenticatedUser, UserEntity entity) {
        if (authenticatedUser == null) {
            return null;
        }
        
        UserResponse.UserResponseBuilder builder = UserResponse.builder()
                .keycloakId(authenticatedUser.getKeycloakId())
                .username(authenticatedUser.getUsername())
                .email(authenticatedUser.getEmail())
                .firstName(authenticatedUser.getFirstName())
                .lastName(authenticatedUser.getLastName())
                .roles(authenticatedUser.getRoles());
        
        // Add entity data if available
        if (entity != null) {
            builder.publicId(entity.getPublicId())
                   .phoneNumber(entity.getPhoneNumber())
                   .address(entity.getAddress())
                   .dateOfBirth(entity.getDateOfBirth())
                   .isActive(entity.getIsActive())
                   .createdAt(entity.getCreatedAt())
                   .updatedAt(entity.getUpdatedAt());
        }
        
        return builder.build();
    }

    public void updateEntityFromSyncRequest(UserEntity entity, UserSyncRequest syncRequest) {
        if (entity == null || syncRequest == null) {
            return;
        }
        
        entity.setKeycloakId(syncRequest.getKeycloakId());
        entity.setUsername(syncRequest.getUsername());
        entity.setEmail(syncRequest.getEmail());
        entity.setFirstName(syncRequest.getFirstName());
        entity.setLastName(syncRequest.getLastName());
        entity.setPhoneNumber(syncRequest.getPhoneNumber());
        entity.setAddress(syncRequest.getAddress());
        entity.setDateOfBirth(syncRequest.getDateOfBirth());
        entity.setIsActive(syncRequest.getIsActive());
        
        // Map roles to primary role
        if (syncRequest.getRoles() != null && !syncRequest.getRoles().isEmpty()) {
            entity.setRole(mapRolesToPrimaryRole(syncRequest.getRoles()));
        }
    }

    public UserEntity toEntity(UserSyncRequest syncRequest) {
        if (syncRequest == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        updateEntityFromSyncRequest(entity, syncRequest);
        return entity;
    }

    public UserEntity toEntity(CreateUserRequest createRequest) {
        if (createRequest == null) {
            return null;
        }
        
        UserEntity entity = new UserEntity();
        entity.setPublicId(UUID.randomUUID());
        entity.setKeycloakId(createRequest.getKeycloakId());
        entity.setUsername(createRequest.getUsername());
        entity.setEmail(createRequest.getEmail());
        entity.setFirstName(createRequest.getFirstName());
        entity.setLastName(createRequest.getLastName());
        entity.setPhoneNumber(createRequest.getPhoneNumber());
        entity.setAddress(createRequest.getAddress());
        entity.setDateOfBirth(createRequest.getDateOfBirth());
        entity.setRole(createRequest.getRole());
        entity.setIsActive(true);
        
        return entity;
    }

    public void updateEntityFromUpdateRequest(UserEntity entity, UpdateUserRequest updateRequest) {
        if (entity == null || updateRequest == null) {
            return;
        }
        
        if (updateRequest.getFirstName() != null) {
            entity.setFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            entity.setLastName(updateRequest.getLastName());
        }
        if (updateRequest.getPhoneNumber() != null) {
            entity.setPhoneNumber(updateRequest.getPhoneNumber());
        }
        if (updateRequest.getAddress() != null) {
            entity.setAddress(updateRequest.getAddress());
        }
        if (updateRequest.getDateOfBirth() != null) {
            entity.setDateOfBirth(updateRequest.getDateOfBirth());
        }
    }

    private Set<String> mapRoleToSet(UserRole role) {
        if (role == null) {
            return Set.of("USER");
        }
        
        return switch (role) {
            case ADMIN -> Set.of("ADMIN", "LIBRARIAN", "USER");
            case LIBRARIAN -> Set.of("LIBRARIAN", "USER");
            case MEMBER -> Set.of("USER");
        };
    }

    private UserRole mapRolesToPrimaryRole(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return UserRole.MEMBER;
        }
        
        // Check for highest privilege roles first
        if (roles.contains("ADMIN")) {
            return UserRole.ADMIN;
        } else if (roles.contains("LIBRARIAN")) {
            return UserRole.LIBRARIAN;
        } else {
            return UserRole.MEMBER;
        }
    }
}