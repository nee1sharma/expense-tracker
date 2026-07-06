package com.hitstudio.expensetracker;

import android.app.Application;

import com.hitstudio.expensetracker.app.AppContainer;

public class ExpenseTrackerApplication extends Application {
    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        container = new AppContainer(this);
        container.workScheduler.scheduleDailyExpenseReminder();
    }

    public AppContainer getContainer() {
        return container;
    }
}
