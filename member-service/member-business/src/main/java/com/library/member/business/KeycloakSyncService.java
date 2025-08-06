package com.library.member.business;

import com.library.member.business.dto.sync.SyncResult;
import org.springframework.transaction.annotation.Transactional;

public interface KeycloakSyncService {
    @Transactional
    SyncResult syncAllUsers();

    @Transactional
    void syncUserById(String keycloakId);

    @Transactional
    void deactivateUser(String keycloakId);
}
