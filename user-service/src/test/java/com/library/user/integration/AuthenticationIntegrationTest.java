package com.library.user.integration;

import com.library.user.application.service.KeycloakUserSyncService;
import com.library.user.application.service.UserContextService;
import com.library.user.domain.model.user.KeycloakId;
import com.library.user.domain.model.user.User;
import com.library.user.domain.repository.UserRepository;
import com.library.user.infrastructure.keycloak.KeycloakUserClient;
import com.library.user.infrastructure.keycloak.dto.KeycloakUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Integration tests for authentication and Keycloak integration
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthenticationIntegrationTest {
    
    @Autowired
    private KeycloakUserSyncService keycloakUserSyncService;
    
    @Autowired
    private UserContextService userContextService;
    
    @Autowired
    private UserRepository userRepository;
    
    @MockBean
    private KeycloakUserClient keycloakUserClient;
    
    @Test
    void testSyncUserFromKeycloak_NewUser() {
        // Given
        String keycloakId = "test-keycloak-id-123";
        KeycloakUserDto keycloakUser = KeycloakUserDto.builder()
            .id(keycloakId)
            .username("testuser")
            .email("test@example.com")
            .firstName("Test")
            .lastName("User")
            .enabled(true)
            .build();
        
        when(keycloakUserClient.getUserById(keycloakId)).thenReturn(keycloakUser);
        
        // When
        User syncedUser = keycloakUserSyncService.syncUserFromKeycloak(keycloakId);
        
        // Then
        assertNotNull(syncedUser);
        assertEquals(keycloakId, syncedUser.getKeycloakId().getValue());
        assertEquals("testuser", syncedUser.getUsername().getValue());
        assertEquals("test@example.com", syncedUser.getEmail().getValue());
        assertEquals("Test", syncedUser.getFirstName().getValue());
        assertEquals("User", syncedUser.getLastName().getValue());
        assertTrue(syncedUser.isActive());
        
        // Verify user is saved in database
        Optional<User> savedUser = userRepository.findByKeycloakId(KeycloakId.of(keycloakId));
        assertTrue(savedUser.isPresent());
        assertEquals(keycloakId, savedUser.get().getKeycloakId().getValue());
    }
    
    @Test
    void testSyncUserFromKeycloak_ExistingUser() {
        // Given
        String keycloakId = "existing-keycloak-id-456";
        
        // Create existing user
        User existingUser = User.createWithKeycloak(
            KeycloakId.of(keycloakId),
            com.library.user.domain.model.user.Username.of("existinguser"),
            com.library.user.domain.model.user.Email.of("existing@example.com"),
            com.library.user.domain.model.user.FirstName.of("Existing"),
            com.library.user.domain.model.user.LastName.of("User"),
            null
        );
        userRepository.save(existingUser);
        
        // Updated Keycloak user data
        KeycloakUserDto updatedKeycloakUser = KeycloakUserDto.builder()
            .id(keycloakId)
            .username("updateduser")
            .email("updated@example.com")
            .firstName("Updated")
            .lastName("User")
            .enabled(true)
            .build();
        
        when(keycloakUserClient.getUserById(keycloakId)).thenReturn(updatedKeycloakUser);
        
        // When
        User syncedUser = keycloakUserSyncService.syncUserFromKeycloak(keycloakId);
        
        // Then
        assertNotNull(syncedUser);
        assertEquals(keycloakId, syncedUser.getKeycloakId().getValue());
        assertEquals("updateduser", syncedUser.getUsername().getValue());
        assertEquals("updated@example.com", syncedUser.getEmail().getValue());
        assertEquals("Updated", syncedUser.getFirstName().getValue());
        assertEquals("User", syncedUser.getLastName().getValue());
    }
    
    @Test
    void testGetCurrentUserContext() {
        // Given
        String keycloakId = "context-test-keycloak-id";
        String username = "contextuser";
        String email = "context@example.com";
        String rolesHeader = "USER,LIBRARIAN";
        String permissionsHeader = "read:books,write:books";
        
        // Create user in database
        User user = User.createWithKeycloak(
            KeycloakId.of(keycloakId),
            com.library.user.domain.model.user.Username.of(username),
            com.library.user.domain.model.user.Email.of(email),
            com.library.user.domain.model.user.FirstName.of("Context"),
            com.library.user.domain.model.user.LastName.of("User"),
            null
        );
        userRepository.save(user);
        
        // When
        UserContextService.UserContext userContext = userContextService.getCurrentUserContext(
            keycloakId, username, email, rolesHeader, permissionsHeader);
        
        // Then
        assertNotNull(userContext);
        assertEquals(keycloakId, userContext.getKeycloakId());
        assertEquals(username, userContext.getUsername());
        assertEquals(email, userContext.getEmail());
        assertEquals(Set.of("USER", "LIBRARIAN"), userContext.getRoles());
        assertEquals(Set.of("read:books", "write:books"), userContext.getPermissions());
        
        assertTrue(userContext.hasRole("USER"));
        assertTrue(userContext.hasRole("LIBRARIAN"));
        assertFalse(userContext.hasRole("ADMIN"));
        
        assertTrue(userContext.hasPermission("read:books"));
        assertTrue(userContext.hasPermission("write:books"));
        assertFalse(userContext.hasPermission("delete:books"));
    }
    
    @Test
    void testGetCurrentUserContext_UserNotFound() {
        // Given
        String keycloakId = "non-existent-keycloak-id";
        String username = "nonexistent";
        String email = "nonexistent@example.com";
        
        KeycloakUserDto keycloakUser = KeycloakUserDto.builder()
            .id(keycloakId)
            .username(username)
            .email(email)
            .firstName("Non")
            .lastName("Existent")
            .enabled(true)
            .build();
        
        when(keycloakUserClient.getUserById(keycloakId)).thenReturn(keycloakUser);
        
        // When
        UserContextService.UserContext userContext = userContextService.getCurrentUserContext(
            keycloakId, username, email, "USER", "");
        
        // Then
        assertNotNull(userContext);
        assertEquals(keycloakId, userContext.getKeycloakId());
        assertEquals(username, userContext.getUsername());
        assertEquals(email, userContext.getEmail());
        
        // Verify user was synced from Keycloak
        Optional<User> syncedUser = userRepository.findByKeycloakId(KeycloakId.of(keycloakId));
        assertTrue(syncedUser.isPresent());
    }
    
    @Test
    void testUserContextService_HasRole() {
        // Given
        UserContextService.UserContext userContext = UserContextService.UserContext.builder()
            .keycloakId("test-id")
            .username("testuser")
            .roles(Set.of("USER", "LIBRARIAN"))
            .build();
        
        // When & Then
        assertTrue(userContextService.hasRole(userContext, "USER"));
        assertTrue(userContextService.hasRole(userContext, "LIBRARIAN"));
        assertFalse(userContextService.hasRole(userContext, "ADMIN"));
        
        assertTrue(userContextService.hasAnyRole(userContext, "USER", "ADMIN"));
        assertTrue(userContextService.hasAnyRole(userContext, "LIBRARIAN", "ADMIN"));
        assertFalse(userContextService.hasAnyRole(userContext, "ADMIN", "SUPER_ADMIN"));
    }
    
    @Test
    void testUserContextService_HasPermission() {
        // Given
        UserContextService.UserContext userContext = UserContextService.UserContext.builder()
            .keycloakId("test-id")
            .username("testuser")
            .permissions(Set.of("read:books", "write:books"))
            .build();
        
        // When & Then
        assertTrue(userContextService.hasPermission(userContext, "read:books"));
        assertTrue(userContextService.hasPermission(userContext, "write:books"));
        assertFalse(userContextService.hasPermission(userContext, "delete:books"));
    }
}