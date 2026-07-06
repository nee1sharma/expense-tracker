package com.hitstudio.expensetracker.util.concurrent;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppExecutors {
    private final ExecutorService diskIO;
    private final Executor mainThread;

    public AppExecutors() {
        diskIO = Executors.newSingleThreadExecutor();
        mainThread = command -> new Handler(Looper.getMainLooper()).post(command);
    }

    public ExecutorService diskIO() {
        return diskIO;
    }

    public Executor mainThread() {
        return mainThread;
    }
}
