package com.hitstudio.expensetracker.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.hitstudio.expensetracker.data.local.room.AppDatabase;
import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.notify.NotificationHelper;
import com.hitstudio.expensetracker.util.PeriodHelper;

public class ExpenseReminderWorker extends Worker {
    public ExpenseReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppDatabase database = AppDatabase.create(getApplicationContext());
        DateRange today = PeriodHelper.today(System.currentTimeMillis());
        int expenseCountToday = database.expenseDao().countInRange(today.startMillis, today.endMillis);
        if (expenseCountToday == 0) {
            new NotificationHelper(getApplicationContext()).showExpenseReminder();
        }
        return Result.success();
    }
}
