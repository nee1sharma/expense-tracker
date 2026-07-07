package com.hitstudio.expensetracker.ui.period;

import java.util.Collections;
import java.util.List;

public class PeriodScreenState {
    public final boolean hasTrackingData;
    public final List<PeriodMonthItem> monthItems;
    public final int selectedIndex;
    public final String selectedMonthLabel;
    public final long selectedMonthTotalMinor;
    public final String currencyCode;

    public PeriodScreenState(boolean hasTrackingData,
                             List<PeriodMonthItem> monthItems,
                             int selectedIndex,
                             String selectedMonthLabel,
                             long selectedMonthTotalMinor,
                             String currencyCode) {
        this.hasTrackingData = hasTrackingData;
        this.monthItems = monthItems == null ? Collections.emptyList() : monthItems;
        this.selectedIndex = selectedIndex;
        this.selectedMonthLabel = selectedMonthLabel == null ? "" : selectedMonthLabel;
        this.selectedMonthTotalMinor = selectedMonthTotalMinor;
        this.currencyCode = currencyCode == null || currencyCode.trim().isEmpty() ? "USD" : currencyCode;
    }

    public static PeriodScreenState empty() {
        return new PeriodScreenState(false, Collections.emptyList(), -1, "", 0L, "USD");
    }
}
