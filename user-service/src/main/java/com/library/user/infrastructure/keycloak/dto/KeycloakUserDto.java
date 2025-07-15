package com.library.user.infrastructure.keycloak.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * DTO for Keycloak user representation
 */
@Data
@Builder
public class KeycloakUserDto {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private boolean emailVerified;
    private List<String> requiredActions;
    private Map<String, List<String>> attributes;
    private List<String> realmRoles;
    private Map<String, List<String>> clientRoles;
    private Long createdTimestamp;
}