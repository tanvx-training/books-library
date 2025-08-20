package com.library.member.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class PermissionsResponse {

    private String keycloakId;

    private String username;

    private Set<String> roles;

    private boolean isAdmin;

    private boolean isLibrarian;

    private boolean isAuthenticated;

    private Set<String> permissions;
}