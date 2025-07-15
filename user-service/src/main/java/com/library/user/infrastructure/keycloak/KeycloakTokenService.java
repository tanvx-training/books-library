package com.library.user.infrastructure.keycloak;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service for managing Keycloak admin access tokens
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakTokenService {
    
    private final RestTemplate restTemplate;
    
    @Value("${keycloak.server-url:http://localhost:8080}")
    private String keycloakServerUrl;
    
    @Value("${keycloak.realm:library-realm}")
    private String realm;
    
    @Value("${keycloak.admin.client-id:admin-cli}")
    private String adminClientId;
    
    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;
    
    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;
    
    private String cachedToken;
    private LocalDateTime tokenExpiry;
    
    /**
     * Get admin access token for Keycloak API calls
     */
    public String getAdminAccessToken() {
        if (isTokenValid()) {
            return cachedToken;
        }
        
        return refreshAdminToken();
    }
    
    private boolean isTokenValid() {
        return cachedToken != null && 
               tokenExpiry != null && 
               LocalDateTime.now().isBefore(tokenExpiry.minusMinutes(1)); // Refresh 1 minute before expiry
    }
    
    private String refreshAdminToken() {
        log.debug("Refreshing Keycloak admin token");
        
        try {
            String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", 
                keycloakServerUrl, realm);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", adminClientId);
            body.add("username", adminUsername);
            body.add("password", adminPassword);
            
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                tokenUrl, HttpMethod.POST, entity, Map.class);
            
            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse != null) {
                cachedToken = (String) tokenResponse.get("access_token");
                Integer expiresIn = (Integer) tokenResponse.get("expires_in");
                
                if (expiresIn != null) {
                    tokenExpiry = LocalDateTime.now().plusSeconds(expiresIn);
                }
                
                log.debug("Keycloak admin token refreshed successfully");
                return cachedToken;
            }
            
            throw new RuntimeException("Failed to get access token from response");
            
        } catch (Exception e) {
            log.error("Error refreshing Keycloak admin token", e);
            throw new RuntimeException("Failed to refresh Keycloak admin token", e);
        }
    }
}