package com.library.catalog.business.security;

import com.library.catalog.business.aop.exception.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class UnifiedAuthenticationService {

    // JWT claim names
    private static final String USER_ID_CLAIM = "sub";
    private static final String USERNAME_CLAIM = "preferred_username";
    private static final String EMAIL_CLAIM = "email";
    private static final String FIRST_NAME_CLAIM = "given_name";
    private static final String LAST_NAME_CLAIM = "family_name";
    private static final String ROLES_CLAIM = "realm_access";
    private static final String ROLES_SUB_CLAIM = "roles";

    public AuthenticatedUser getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();
                return extractUserFromJwt(jwt);
            }
            
            // For API Gateway requests, user info should be in the JWT token
            // The SecurityContextFilter should have already processed headers
            return null;
            
        } catch (Exception e) {
            log.error("Error extracting current user", e);
            return null;
        }
    }

    public String getCurrentUserKeycloakId() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.getKeycloakId() : "SYSTEM";
    }


    public boolean hasRole(String role) {
        AuthenticatedUser user = getCurrentUser();
        return user != null && user.hasRole(role);
    }

    public boolean hasAnyRole(String... roles) {
        AuthenticatedUser user = getCurrentUser();
        return user != null && user.hasAnyRole(roles);
    }

    public void validateAuthentication() {
        AuthenticatedUser user = getCurrentUser();
        if (user == null) {
            throw new AuthenticationException("No authenticated user found");
        }
        
        if (!StringUtils.hasText(user.getKeycloakId())) {
            throw new AuthenticationException("Invalid user context: missing user ID");
        }
    }

    public boolean isAuthenticated() {
        return getCurrentUser() != null;
    }

    private AuthenticatedUser extractUserFromJwt(Jwt jwt) {
        if (jwt == null) {
            return null;
        }

        try {
            String keycloakId = jwt.getClaimAsString(USER_ID_CLAIM);
            String username = jwt.getClaimAsString(USERNAME_CLAIM);
            String email = jwt.getClaimAsString(EMAIL_CLAIM);
            String firstName = jwt.getClaimAsString(FIRST_NAME_CLAIM);
            String lastName = jwt.getClaimAsString(LAST_NAME_CLAIM);
            Set<String> roles = extractRolesFromJwt(jwt);

            if (!StringUtils.hasText(keycloakId)) {
                log.warn("JWT token missing required user ID claim");
                return null;
            }

            return AuthenticatedUser.builder()
                    .keycloakId(keycloakId.trim())
                    .username(StringUtils.hasText(username) ? username.trim() : null)
                    .email(StringUtils.hasText(email) ? email.trim() : null)
                    .firstName(StringUtils.hasText(firstName) ? firstName.trim() : null)
                    .lastName(StringUtils.hasText(lastName) ? lastName.trim() : null)
                    .roles(roles)
                    .customClaims(jwt.getClaims())
                    .build();

        } catch (Exception e) {
            log.error("Error extracting user from JWT", e);
            return null;
        }
    }

    private Set<String> extractRolesFromJwt(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        
        try {
            Map<String, Object> realmAccess = jwt.getClaimAsMap(ROLES_CLAIM);
            if (realmAccess != null && realmAccess.containsKey(ROLES_SUB_CLAIM)) {
                Object rolesObj = realmAccess.get(ROLES_SUB_CLAIM);
                if (rolesObj instanceof List<?> rolesList) {
                    for (Object role : rolesList) {
                        if (role instanceof String roleStr && StringUtils.hasText(roleStr)) {
                            roles.add(roleStr.trim());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error extracting roles from JWT", e);
        }
        
        return roles;
    }
}