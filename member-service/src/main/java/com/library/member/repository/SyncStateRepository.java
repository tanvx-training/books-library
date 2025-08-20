package com.library.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SyncStateRepository extends JpaRepository<SyncStateEntity, Long> {

    @Query("SELECT s FROM SyncStateEntity s WHERE s.syncType = ?1 ORDER BY s.createdAt DESC LIMIT 1")
    Optional<SyncStateEntity> findLatestBySyncType(String syncType);

    @Query("SELECT s FROM SyncStateEntity s ORDER BY s.createdAt DESC LIMIT 1")
    Optional<SyncStateEntity> findLatest();
}