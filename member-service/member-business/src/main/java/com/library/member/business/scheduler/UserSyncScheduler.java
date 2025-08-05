package com.library.member.business.scheduler;

import com.library.member.business.KeycloakSyncService;
import com.library.member.business.dto.sync.SyncResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(value = "member-service.sync.enabled", havingValue = "true", matchIfMissing = true)
public class UserSyncScheduler {

    private final KeycloakSyncService syncService;

    @Scheduled(fixedDelayString = "${member-service.sync.schedule.fixed-delay:300000}") // Default 5 minutes
    public void syncUsers() {
        log.info("Starting scheduled user synchronization");

        try {
            SyncResult result = syncService.syncAllUsers();
            
            if (result.isSuccessful()) {
                log.info("User sync completed successfully - {} users processed in {} ms",
                        result.getTotalCount(),
                        java.time.Duration.between(result.getStartTime(), result.getEndTime()).toMillis());
            } else {
                log.warn("User sync completed with errors - Success: {}, Failed: {}, Success Rate: {}%",
                        result.getSuccessCount(),
                        result.getFailureCount(),
                        result.getSuccessRate());
                
                if (!result.getErrors().isEmpty()) {
                    log.warn("Sync errors: {}", result.getErrors());
                }
            }

        } catch (Exception e) {
            log.error("Scheduled user synchronization failed", e);
        }
    }

    @Scheduled(cron = "${member-service.sync.reconciliation.cron:0 0 2 * * ?}") // Default daily at 2 AM
    public void reconcileUsers() {
        log.info("Starting user reconciliation job");

        try {
            SyncResult result = syncService.syncAllUsers();
            log.info("User reconciliation completed - Success: {}, Failed: {}",
                    result.getSuccessCount(), result.getFailureCount());

        } catch (Exception e) {
            log.error("User reconciliation failed", e);
        }
    }
}