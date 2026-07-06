package com.hitstudio.expensetracker.domain.model;

public class DateRange {
    public final long startMillis;
    public final long endMillis;

    public DateRange(long startMillis, long endMillis) {
        this.startMillis = startMillis;
        this.endMillis = endMillis;
    }

    public boolean contains(long millis) {
        return millis >= startMillis && millis < endMillis;
    }
}
