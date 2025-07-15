package com.library.user.infrastructure.keycloak;

import com.library.user.infrastructure.keycloak.dto.KeycloakUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of Keycloak user client using REST API
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakUserClientImpl implements KeycloakUserClient {
    
    private final RestTemplate restTemplate;
    private final KeycloakTokenService keycloakTokenService;
    
    @Value("${keycloak.server-url:http://localhost:8080}")
    private String keycloakServerUrl;
    
    @Value("${keycloak.realm:library-realm}")
    private String realm;
    
    @Override
    public KeycloakUserDto getUserById(String keycloakId) {
        log.debug("Getting user by ID from Keycloak: {}", keycloakId);
        
        try {
            String url = String.format("%s/admin/realms/%s/users/%s", 
                keycloakServerUrl, realm, keycloakId);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<KeycloakUserDto> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, KeycloakUserDto.class);
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error getting user by ID from Keycloak: {}", keycloakId, e);
            throw new RuntimeException("Failed to get user from Keycloak", e);
        }
    }
    
    @Override
    public KeycloakUserDto getUserByUsername(String username) {
        log.debug("Getting user by username from Keycloak: {}", username);
        
        try {
            String url = String.format("%s/admin/realms/%s/users?username=%s", 
                keycloakServerUrl, realm, username);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List<KeycloakUserDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<KeycloakUserDto>>() {});
            
            List<KeycloakUserDto> users = response.getBody();
            return users != null && !users.isEmpty() ? users.get(0) : null;
            
        } catch (Exception e) {
            log.error("Error getting user by username from Keycloak: {}", username, e);
            throw new RuntimeException("Failed to get user from Keycloak", e);
        }
    }
    
    @Override
    public KeycloakUserDto getUserByEmail(String email) {
        log.debug("Getting user by email from Keycloak: {}", email);
        
        try {
            String url = String.format("%s/admin/realms/%s/users?email=%s", 
                keycloakServerUrl, realm, email);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List<KeycloakUserDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<KeycloakUserDto>>() {});
            
            List<KeycloakUserDto> users = response.getBody();
            return users != null && !users.isEmpty() ? users.get(0) : null;
            
        } catch (Exception e) {
            log.error("Error getting user by email from Keycloak: {}", email, e);
            throw new RuntimeException("Failed to get user from Keycloak", e);
        }
    }
    
    @Override
    public List<KeycloakUserDto> getAllUsers() {
        log.debug("Getting all users from Keycloak");
        
        try {
            String url = String.format("%s/admin/realms/%s/users", 
                keycloakServerUrl, realm);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List<KeycloakUserDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<KeycloakUserDto>>() {});
            
            return response.getBody();
            
        } catch (Exception e) {
            log.error("Error getting all users from Keycloak", e);
            throw new RuntimeException("Failed to get users from Keycloak", e);
        }
    }
    
    @Override
    public String createUser(KeycloakUserDto userDto) {
        log.debug("Creating user in Keycloak: {}", userDto.getUsername());
        
        try {
            String url = String.format("%s/admin/realms/%s/users", 
                keycloakServerUrl, realm);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<KeycloakUserDto> entity = new HttpEntity<>(userDto, headers);
            
            ResponseEntity<Void> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Void.class);
            
            // Extract user ID from Location header
            String location = response.getHeaders().getFirst("Location");
            if (location != null) {
                return location.substring(location.lastIndexOf('/') + 1);
            }
            
            throw new RuntimeException("Failed to get user ID from response");
            
        } catch (Exception e) {
            log.error("Error creating user in Keycloak: {}", userDto.getUsername(), e);
            throw new RuntimeException("Failed to create user in Keycloak", e);
        }
    }
    
    @Override
    public void updateUser(String keycloakId, KeycloakUserDto userDto) {
        log.debug("Updating user in Keycloak: {}", keycloakId);
        
        try {
            String url = String.format("%s/admin/realms/%s/users/%s", 
                keycloakServerUrl, realm, keycloakId);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<KeycloakUserDto> entity = new HttpEntity<>(userDto, headers);
            
            restTemplate.exchange(url, HttpMethod.PUT, entity, Void.class);
            
        } catch (Exception e) {
            log.error("Error updating user in Keycloak: {}", keycloakId, e);
            throw new RuntimeException("Failed to update user in Keycloak", e);
        }
    }
    
    @Override
    public void deleteUser(String keycloakId) {
        log.debug("Deleting user from Keycloak: {}", keycloakId);
        
        try {
            String url = String.format("%s/admin/realms/%s/users/%s", 
                keycloakServerUrl, realm, keycloakId);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            
        } catch (Exception e) {
            log.error("Error deleting user from Keycloak: {}", keycloakId, e);
            throw new RuntimeException("Failed to delete user from Keycloak", e);
        }
    }
    
    @Override
    public void setUserEnabled(String keycloakId, boolean enabled) {
        log.debug("Setting user enabled status in Keycloak: {} -> {}", keycloakId, enabled);
        
        try {
            KeycloakUserDto userDto = KeycloakUserDto.builder()
                .enabled(enabled)
                .build();
            
            updateUser(keycloakId, userDto);
            
        } catch (Exception e) {
            log.error("Error setting user enabled status in Keycloak: {}", keycloakId, e);
            throw new RuntimeException("Failed to set user enabled status in Keycloak", e);
        }
    }
    
    @Override
    public List<String> getUserRoles(String keycloakId) {
        log.debug("Getting user roles from Keycloak: {}", keycloakId);
        
        try {
            String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm", 
                keycloakServerUrl, realm, keycloakId);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Map<String, Object>>>() {});
            
            List<Map<String, Object>> roles = response.getBody();
            return roles != null ? roles.stream()
                .map(role -> (String) role.get("name"))
                .toList() : List.of();
            
        } catch (Exception e) {
            log.error("Error getting user roles from Keycloak: {}", keycloakId, e);
            throw new RuntimeException("Failed to get user roles from Keycloak", e);
        }
    }
    
    @Override
    public void assignRoleToUser(String keycloakId, String roleName) {
        log.debug("Assigning role to user in Keycloak: {} -> {}", keycloakId, roleName);
        
        try {
            // Implementation would require getting role representation first
            // This is a simplified version
            String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm", 
                keycloakServerUrl, realm, keycloakId);
            
            Map<String, Object> role = new HashMap<>();
            role.put("name", roleName);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(List.of(role), headers);
            
            restTemplate.exchange(url, HttpMethod.POST, entity, Void.class);
            
        } catch (Exception e) {
            log.error("Error assigning role to user in Keycloak: {} -> {}", keycloakId, roleName, e);
            throw new RuntimeException("Failed to assign role to user in Keycloak", e);
        }
    }
    
    @Override
    public void removeRoleFromUser(String keycloakId, String roleName) {
        log.debug("Removing role from user in Keycloak: {} -> {}", keycloakId, roleName);
        
        try {
            // Implementation would require getting role representation first
            // This is a simplified version
            String url = String.format("%s/admin/realms/%s/users/%s/role-mappings/realm", 
                keycloakServerUrl, realm, keycloakId);
            
            Map<String, Object> role = new HashMap<>();
            role.put("name", roleName);
            
            HttpHeaders headers = createAuthHeaders();
            HttpEntity<List<Map<String, Object>>> entity = new HttpEntity<>(List.of(role), headers);
            
            restTemplate.exchange(url, HttpMethod.DELETE, entity, Void.class);
            
        } catch (Exception e) {
            log.error("Error removing role from user in Keycloak: {} -> {}", keycloakId, roleName, e);
            throw new RuntimeException("Failed to remove role from user in Keycloak", e);
        }
    }
    
    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(keycloakTokenService.getAdminAccessToken());
        return headers;
    }
}