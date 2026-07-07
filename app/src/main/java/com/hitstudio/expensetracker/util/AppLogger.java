package com.hitstudio.expensetracker.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple logger that captures errors and uncaught exceptions to a file in the app's cache.
 * These logs can then be included in bug reports.
 */
public class AppLogger {
    private static final String TAG = "AppLogger";
    private static final String LOG_FILE_NAME = "crash_logs.txt";
    private static final int MAX_FILE_SIZE = 1024 * 50; // 50KB

    private static File logFile;

    public static void init(Context context) {
        logFile = new File(context.getCacheDir(), LOG_FILE_NAME);
        setupUncaughtExceptionHandler();
    }

    public static void e(String tag, String message, Throwable throwable) {
        Log.e(tag, message, throwable);
        logToFile("ERROR", tag, message, throwable);
    }

    public static void i(String tag, String message) {
        Log.i(tag, message);
        logToFile("INFO", tag, message, null);
    }

    public static String getCapturedLogs() {
        if (logFile == null || !logFile.exists()) {
            return "No captured logs found.\n";
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            return "Failed to read logs: " + e.getMessage();
        }
        return sb.toString();
    }

    private static void setupUncaughtExceptionHandler() {
        Thread.UncaughtExceptionHandler defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            e(TAG, "CRASH: Uncaught exception on thread " + thread.getName(), throwable);
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });
    }

    private static synchronized void logToFile(String level, String tag, String message, Throwable throwable) {
        if (logFile == null) return;

        // Simple size management: if file too large, delete it and start over
        if (logFile.length() > MAX_FILE_SIZE) {
            logFile.delete();
        }

        try (FileOutputStream fos = new FileOutputStream(logFile, true);
             PrintWriter writer = new PrintWriter(fos)) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US).format(new Date());
            writer.println(String.format("[%s] %s/%s: %s", timestamp, level, tag, message));
            if (throwable != null) {
                throwable.printStackTrace(writer);
            }
            writer.flush();
        } catch (IOException e) {
            Log.e(TAG, "Failed to write to log file", e);
        }
    }
}
