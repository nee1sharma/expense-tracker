package com.hitstudio.expensetracker.util;

import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.domain.model.PeriodType;

import java.util.Calendar;

public class PeriodHelper {
    private PeriodHelper() {
    }

    public static DateRange today(long anchorMillis) {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(anchorMillis);
        clearTime(start);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, 1);
        return new DateRange(start.getTimeInMillis(), end.getTimeInMillis());
    }

    public static DateRange thisWeek(long anchorMillis, int firstDayOfWeek) {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(anchorMillis);
        start.setFirstDayOfWeek(firstDayOfWeek);
        clearTime(start);
        while (start.get(Calendar.DAY_OF_WEEK) != firstDayOfWeek) {
            start.add(Calendar.DAY_OF_MONTH, -1);
        }
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.WEEK_OF_YEAR, 1);
        return new DateRange(start.getTimeInMillis(), end.getTimeInMillis());
    }

    public static DateRange thisMonth(long anchorMillis) {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(anchorMillis);
        clearTime(start);
        start.set(Calendar.DAY_OF_MONTH, 1);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);
        return new DateRange(start.getTimeInMillis(), end.getTimeInMillis());
    }

    public static DateRange period(PeriodType type, long anchorMillis, int firstDayOfWeek) {
        if (type == PeriodType.WEEK) {
            return thisWeek(anchorMillis, firstDayOfWeek);
        }
        if (type == PeriodType.YEAR) {
            Calendar start = Calendar.getInstance();
            start.setTimeInMillis(anchorMillis);
            clearTime(start);
            start.set(Calendar.DAY_OF_YEAR, 1);
            Calendar end = (Calendar) start.clone();
            end.add(Calendar.YEAR, 1);
            return new DateRange(start.getTimeInMillis(), end.getTimeInMillis());
        }
        return thisMonth(anchorMillis);
    }

    private static void clearTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
