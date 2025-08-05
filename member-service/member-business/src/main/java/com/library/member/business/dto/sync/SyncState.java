package com.library.member.business.dto.sync;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncState {
    private Long id;
    private String syncType;
    private LocalDateTime lastSyncTime;
    private Integer syncedUserCount;
    private Integer successCount;
    private Integer failureCount;
    private LocalDateTime createdAt;

    public SyncState(LocalDateTime lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
        this.syncType = "SCHEDULED";
        this.createdAt = LocalDateTime.now();
    }
}