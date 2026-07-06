package com.hitstudio.expensetracker.data.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.hitstudio.expensetracker.domain.model.AppSettings;
import com.hitstudio.expensetracker.domain.model.PaymentMethod;
import com.hitstudio.expensetracker.domain.model.StorageMode;

import java.util.Calendar;

public class AppPreferences {
    private static final String PREFS = "expense_tracker_settings";
    private static final String KEY_CURRENCY = "default_currency";
    private static final String KEY_PAYMENT = "default_payment";
    private static final String KEY_STORAGE = "storage_mode";
    private static final String KEY_INCLUDE_INCOME = "include_income_dashboard";
    private static final String KEY_FIRST_DAY = "first_day_of_week";

    private final SharedPreferences prefs;

    public AppPreferences(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public AppSettings getSettings() {
        return new AppSettings(
                prefs.getString(KEY_CURRENCY, "USD"),
                enumValue(PaymentMethod.class, prefs.getString(KEY_PAYMENT, PaymentMethod.CASH.name()), PaymentMethod.CASH),
                enumValue(StorageMode.class, prefs.getString(KEY_STORAGE, StorageMode.ROOM_LOCAL.name()), StorageMode.ROOM_LOCAL),
                prefs.getBoolean(KEY_INCLUDE_INCOME, false),
                prefs.getInt(KEY_FIRST_DAY, Calendar.MONDAY)
        );
    }

    public void saveSettings(AppSettings settings) {
        prefs.edit()
                .putString(KEY_CURRENCY, settings.defaultCurrencyCode)
                .putString(KEY_PAYMENT, settings.defaultPaymentMethod.name())
                .putString(KEY_STORAGE, settings.storageMode.name())
                .putBoolean(KEY_INCLUDE_INCOME, settings.includeIncomeOnDashboard)
                .putInt(KEY_FIRST_DAY, settings.firstDayOfWeek)
                .apply();
    }

    private static <T extends Enum<T>> T enumValue(Class<T> type, String value, T fallback) {
        try {
            return Enum.valueOf(type, value);
        } catch (IllegalArgumentException | NullPointerException ignored) {
            return fallback;
        }
    }
}
