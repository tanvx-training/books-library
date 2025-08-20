package com.library.member.dto.sync;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SyncResult {
    private int successCount = 0;
    private int failureCount = 0;
    private int totalCount = 0;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> errors = new ArrayList<>();

    public SyncResult() {
        this.startTime = LocalDateTime.now();
    }

    public void incrementSuccess() {
        this.successCount++;
        this.totalCount++;
    }

    public void incrementFailure() {
        this.failureCount++;
        this.totalCount++;
    }

    public void addError(String error) {
        this.errors.add(error);
    }

    public void complete() {
        this.endTime = LocalDateTime.now();
    }

    public boolean isSuccessful() {
        return failureCount == 0;
    }

    public double getSuccessRate() {
        return totalCount > 0 ? (double) successCount / totalCount * 100 : 0;
    }
}