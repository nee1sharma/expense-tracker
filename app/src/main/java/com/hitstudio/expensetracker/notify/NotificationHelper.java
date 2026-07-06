package com.hitstudio.expensetracker.notify;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.hitstudio.expensetracker.MainActivity;
import com.hitstudio.expensetracker.R;

public class NotificationHelper {
    public static final String CHANNEL_ID = "expense_reminders";
    public static final int REMINDER_NOTIFICATION_ID = 1001;
    public static final String EXTRA_OPEN_LOGGER = "open_logger_from_notification";

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
    }

    public Context getContext() {
        return context;
    }

    public void showExpenseReminder() {
        if (!canPostNotifications()) {
            return;
        }
        ensureChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_info)
                .setContentTitle(context.getString(R.string.reminder_notification_title))
                .setContentText(context.getString(R.string.reminder_notification_text))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(R.string.reminder_notification_text)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .setContentIntent(createContentIntent());

        NotificationManagerCompat.from(context).notify(REMINDER_NOTIFICATION_ID, builder.build());
    }

    private PendingIntent createContentIntent() {
        Intent intent = new Intent(context, MainActivity.class)
                .setAction(MainActivity.ACTION_OPEN_LOGGER)
                .putExtra(EXTRA_OPEN_LOGGER, true)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        return PendingIntent.getActivity(
                context,
                REMINDER_NOTIFICATION_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private void ensureChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.reminder_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setDescription(context.getString(R.string.reminder_channel_description));
        manager.createNotificationChannel(channel);
    }

    private boolean canPostNotifications() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true;
        }
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }
}
