package com.hitstudio.expensetracker.work;

import android.content.Context;

import androidx.work.WorkManager;

public class WorkScheduler {
    private final WorkManager workManager;

    public WorkScheduler(Context context) {
        workManager = WorkManager.getInstance(context.getApplicationContext());
    }

    public WorkManager getWorkManager() {
        return workManager;
    }
}
