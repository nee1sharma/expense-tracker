package com.hitstudio.expensetracker.util;

import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

public class MoneyFormatter {
    private MoneyFormatter() {
    }

    public static String format(long amountMinor, String currencyCode) {
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        try {
            format.setCurrency(Currency.getInstance(currencyCode));
        } catch (IllegalArgumentException ignored) {
            format.setCurrency(Currency.getInstance("USD"));
        }
        return format.format(amountMinor / 100.0d);
    }
}
