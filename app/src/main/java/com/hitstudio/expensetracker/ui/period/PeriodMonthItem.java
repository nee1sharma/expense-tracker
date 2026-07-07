package com.hitstudio.expensetracker.ui.period;

public class PeriodMonthItem {
    public final long startMillis;
    public final long endMillis;
    public final String label;
    public final long totalMinor;
    public final String currencyCode;
    public final boolean hasExpenses;

    public PeriodMonthItem(long startMillis, long endMillis, String label, long totalMinor, String currencyCode, boolean hasExpenses) {
        this.startMillis = startMillis;
        this.endMillis = endMillis;
        this.label = label;
        this.totalMinor = totalMinor;
        this.currencyCode = currencyCode;
        this.hasExpenses = hasExpenses;
    }
}
