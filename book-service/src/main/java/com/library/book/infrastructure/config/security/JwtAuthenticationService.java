package com.library.book.infrastructure.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for handling JWT-based authentication and authorization
 */
@Slf4j
@Service
public class JwtAuthenticationService {

    /**
     * Get current authenticated user information from JWT token
     */
    public AuthenticatedUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.debug("No authenticated user found");
            return null;
        }
        
        if (!(authentication instanceof JwtAuthenticationToken jwtToken)) {
            log.debug("Authentication is not JWT-based");
            return null;
        }
        
        try {
            Jwt jwt = jwtToken.getToken();
            return extractUserFromJwt(jwt, authentication.getAuthorities());
        } catch (Exception e) {
            log.error("Error extracting user from JWT token", e);
            return null;
        }
    }
    
    /**
     * Check if current user has specific role
     */
    public boolean hasRole(String role) {
        AuthenticatedUser user = getCurrentUser();
        return user != null && user.hasRole(role);
    }
    
    /**
     * Check if current user has any of the specified roles
     */
    public boolean hasAnyRole(String... roles) {
        AuthenticatedUser user = getCurrentUser();
        if (user == null) {
            return false;
        }
        
        for (String role : roles) {
            if (user.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user has specific authority
     */
    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
    
    /**
     * Get current user's Keycloak ID
     */
    public String getCurrentUserId() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.getKeycloakId() : null;
    }
    
    /**
     * Get current user's username
     */
    public String getCurrentUsername() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.getUsername() : null;
    }
    
    /**
     * Get current user's email
     */
    public String getCurrentUserEmail() {
        AuthenticatedUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }
    
    /**
     * Check if current user can manage books
     */
    public boolean canManageBooks() {
        return hasAnyRole("ADMIN", "LIBRARIAN");
    }
    
    /**
     * Check if current user can borrow books
     */
    public boolean canBorrowBooks() {
        return hasAnyRole("USER", "LIBRARIAN", "ADMIN");
    }
    
    /**
     * Check if current user is admin
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }
    
    /**
     * Check if current user is librarian
     */
    public boolean isLibrarian() {
        return hasRole("LIBRARIAN");
    }
    
    /**
     * Check if current user is regular user
     */
    public boolean isUser() {
        return hasRole("USER");
    }
    
    /**
     * Extract user information from JWT token
     */
    private AuthenticatedUser extractUserFromJwt(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        // Extract basic user information
        String keycloakId = jwt.getSubject(); // 'sub' claim contains Keycloak user ID
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        String firstName = jwt.getClaimAsString("given_name");
        String lastName = jwt.getClaimAsString("family_name");
        String fullName = jwt.getClaimAsString("name");
        
        // Extract roles from authorities
        Set<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5)) // Remove "ROLE_" prefix
                .collect(Collectors.toSet());
        
        // Extract additional claims
        Map<String, Object> customClaims = extractCustomClaims(jwt);
        
        return AuthenticatedUser.builder()
                .keycloakId(keycloakId)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(fullName)
                .roles(roles)
                .authorities(authorities.stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .customClaims(customClaims)
                .build();
    }
    
    /**
     * Extract custom claims from JWT token
     */
    private Map<String, Object> extractCustomClaims(Jwt jwt) {
        return jwt.getClaims().entrySet().stream()
                .filter(entry -> !isStandardClaim(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
    
    /**
     * Check if claim is a standard JWT claim
     */
    private boolean isStandardClaim(String claimName) {
        Set<String> standardClaims = Set.of(
                "iss", "sub", "aud", "exp", "nbf", "iat", "jti",
                "preferred_username", "email", "given_name", "family_name", "name",
                "realm_access", "resource_access", "scope", "email_verified",
                "azp", "session_state", "acr", "allowed-origins", "typ"
        );
        return standardClaims.contains(claimName);
    }
    
    /**
     * Authenticated user data class
     */
    @lombok.Builder
    @lombok.Data
    public static class AuthenticatedUser {
        private String keycloakId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private String fullName;
        private Set<String> roles;
        private Set<String> authorities;
        private Map<String, Object> customClaims;
        
        public boolean hasRole(String role) {
            return roles != null && (roles.contains(role) || roles.contains(role.toUpperCase()));
        }
        
        public boolean hasAuthority(String authority) {
            return authorities != null && authorities.contains(authority);
        }
        
        public boolean isAdmin() {
            return hasRole("ADMIN");
        }
        
        public boolean isLibrarian() {
            return hasRole("LIBRARIAN");
        }
        
        public boolean isUser() {
            return hasRole("USER");
        }
        
        public boolean canManageBooks() {
            return isAdmin() || isLibrarian();
        }
        
        public boolean canBorrowBooks() {
            return isUser() || isLibrarian() || isAdmin();
        }
        
        public String getDisplayName() {
            if (fullName != null && !fullName.trim().isEmpty()) {
                return fullName;
            }
            if (firstName != null && lastName != null) {
                return firstName + " " + lastName;
            }
            return username != null ? username : email;
        }
    }
}