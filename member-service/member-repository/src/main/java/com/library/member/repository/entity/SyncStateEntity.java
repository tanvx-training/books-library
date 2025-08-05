package com.library.member.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sync_states")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sync_type", nullable = false, length = 50)
    private String syncType;

    @Column(name = "last_sync_time", nullable = false)
    private LocalDateTime lastSyncTime;

    @Column(name = "synced_user_count")
    private Integer syncedUserCount;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "failure_count")
    private Integer failureCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}