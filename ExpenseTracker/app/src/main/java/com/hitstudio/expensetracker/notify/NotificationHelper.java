package com.hitstudio.expensetracker.notify;

import android.content.Context;

public class NotificationHelper {
    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public Context getContext() {
        return context;
    }
}
