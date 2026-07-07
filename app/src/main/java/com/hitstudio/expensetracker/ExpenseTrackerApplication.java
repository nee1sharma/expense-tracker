package com.hitstudio.expensetracker;

import android.app.Application;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.util.AppLogger;

public class ExpenseTrackerApplication extends Application {
    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        AppLogger.init(this);
        container = new AppContainer(this);
        container.workScheduler.scheduleDailyExpenseReminder();
    }

    public AppContainer getContainer() {
        return container;
    }
}
