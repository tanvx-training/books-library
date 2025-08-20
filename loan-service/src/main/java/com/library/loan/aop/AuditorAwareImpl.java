package com.library.loan.aop;

import com.library.loan.service.UnifiedAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

    private final UnifiedAuthenticationService authenticationService;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String currentUserKeycloakId = authenticationService.getCurrentUserKeycloakId();

            if (currentUserKeycloakId != null && !currentUserKeycloakId.equals("SYSTEM")) {
                log.debug("Current auditor: {}", currentUserKeycloakId);
                return Optional.of(currentUserKeycloakId);
            }

            log.debug("No authenticated user found, using SYSTEM as auditor");
            return Optional.of("SYSTEM");

        } catch (Exception e) {
            log.debug("Error getting current auditor, using SYSTEM: {}", e.getMessage());
            return Optional.of("SYSTEM");
        }
    }
}