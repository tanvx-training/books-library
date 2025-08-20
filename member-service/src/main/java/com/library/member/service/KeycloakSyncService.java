package com.library.member.service;

import com.library.member.dto.sync.SyncResult;
import org.springframework.transaction.annotation.Transactional;

public interface KeycloakSyncService {
    @Transactional
    SyncResult syncAllUsers();

    @Transactional
    void syncUserById(String keycloakId);

    @Transactional
    void deactivateUser(String keycloakId);
}
