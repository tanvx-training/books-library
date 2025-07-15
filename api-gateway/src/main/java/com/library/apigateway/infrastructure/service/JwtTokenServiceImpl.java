package com.library.apigateway.infrastructure.service;

import com.library.apigateway.domain.model.UserContext;
import com.library.apigateway.domain.service.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Infrastructure implementation of JWT token service
 */
@Slf4j
@Service
public class JwtTokenServiceImpl implements JwtTokenService {
    
    @Override
    public UserContext extractUserContext(Jwt jwt) {
        try {
            return UserContext.builder()
                .keycloakId(extractKeycloakId(jwt))
                .username(extractUsername(jwt))
                .email(jwt.getClaimAsString("email"))
                .firstName(jwt.getClaimAsString("given_name"))
                .lastName(jwt.getClaimAsString("family_name"))
                .roles(extractRoles(jwt))
                .permissions(extractPermissions(jwt))
                .isActive(true)
                .build();
        } catch (Exception e) {
            log.error("Error extracting user context from JWT", e);
            return null;
        }
    }
    
    @Override
    public boolean isTokenValid(String token) {
        try {
            // Token validation is handled by Spring Security OAuth2 Resource Server
            return token != null && !token.trim().isEmpty();
        } catch (Exception e) {
            log.error("Error validating token", e);
            return false;
        }
    }
    
    @Override
    public String extractKeycloakId(Jwt jwt) {
        return jwt.getClaimAsString("sub");
    }
    
    @Override
    public String extractUsername(Jwt jwt) {
        return jwt.getClaimAsString("preferred_username");
    }
    
    @Override
    public Set<String> extractRoles(Jwt jwt) {
        Set<String> roles = new HashSet<>();
        
        // Extract realm roles
        Object realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> realmAccessMap = (java.util.Map<String, Object>) realmAccess;
            Object rolesObj = realmAccessMap.get("roles");
            if (rolesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> rolesList = (List<String>) rolesObj;
                roles.addAll(rolesList);
            }
        }
        
        // Extract resource roles
        Object resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> resourceAccessMap = (java.util.Map<String, Object>) resourceAccess;
            
            // Extract roles from api-gateway client
            Object apiGatewayAccess = resourceAccessMap.get("api-gateway");
            if (apiGatewayAccess instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> clientAccessMap = (java.util.Map<String, Object>) apiGatewayAccess;
                Object clientRolesObj = clientAccessMap.get("roles");
                if (clientRolesObj instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> clientRolesList = (List<String>) clientRolesObj;
                    roles.addAll(clientRolesList);
                }
            }
        }
        
        return roles;
    }
    
    private Set<String> extractPermissions(Jwt jwt) {
        Set<String> permissions = new HashSet<>();
        
        // Extract permissions from custom claims if available
        Object permissionsClaim = jwt.getClaim("permissions");
        if (permissionsClaim instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> permissionsList = (List<String>) permissionsClaim;
            permissions.addAll(permissionsList);
        }
        
        return permissions;
    }
}