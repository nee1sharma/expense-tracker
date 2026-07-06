package com.hitstudio.expensetracker.domain.model;

public enum SyncStatus {
    LOCAL_ONLY,
    PENDING_UPLOAD,
    SYNCED,
    FAILED
}
