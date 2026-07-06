package com.hitstudio.expensetracker.work;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkScheduler {
    public static final String DAILY_REMINDER_WORK_NAME = "daily_expense_reminder";

    private static final int REMINDER_HOUR_OF_DAY = 20;

    private final Context context;
    private final WorkManager workManager;

    public WorkScheduler(Context context) {
        this.context = context.getApplicationContext();
        workManager = WorkManager.getInstance(this.context);
    }

    public WorkManager getWorkManager() {
        return workManager;
    }

    public void scheduleDailyExpenseReminder() {
        long initialDelay = computeInitialDelayMillis();
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(ExpenseReminderWorker.class, 1, TimeUnit.DAYS)
                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
                .build();
        workManager.enqueueUniquePeriodicWork(
                DAILY_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
        );
    }

    private long computeInitialDelayMillis() {
        Calendar nextRun = Calendar.getInstance();
        nextRun.set(Calendar.HOUR_OF_DAY, REMINDER_HOUR_OF_DAY);
        nextRun.set(Calendar.MINUTE, 0);
        nextRun.set(Calendar.SECOND, 0);
        nextRun.set(Calendar.MILLISECOND, 0);
        if (nextRun.getTimeInMillis() <= System.currentTimeMillis()) {
            nextRun.add(Calendar.DAY_OF_MONTH, 1);
        }
        return nextRun.getTimeInMillis() - System.currentTimeMillis();
    }
}
