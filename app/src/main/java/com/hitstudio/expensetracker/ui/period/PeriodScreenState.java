package com.hitstudio.expensetracker.ui.period;

import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.Collections;
import java.util.List;

public class PeriodScreenState {
    public final boolean hasTrackingData;
    public final String yearLabel;
    public final long yearTotalMinor;
    public final String yearCurrencyCode;
    public final List<PeriodCategoryItem> yearCategories;
    public final List<PeriodMonthItem> monthItems;
    public final int selectedIndex;
    public final String selectedMonthLabel;
    public final long selectedMonthTotalMinor;
    public final String currencyCode;
    public final List<PeriodCategoryItem> selectedMonthCategories;
    public final List<Expense> selectedMonthExpenses;

    public PeriodScreenState(boolean hasTrackingData,
                             String yearLabel,
                             long yearTotalMinor,
                             String yearCurrencyCode,
                             List<PeriodCategoryItem> yearCategories,
                             List<PeriodMonthItem> monthItems,
                             int selectedIndex,
                             String selectedMonthLabel,
                             long selectedMonthTotalMinor,
                             String currencyCode,
                             List<PeriodCategoryItem> selectedMonthCategories,
                             List<Expense> selectedMonthExpenses) {
        this.hasTrackingData = hasTrackingData;
        this.yearLabel = yearLabel == null ? "" : yearLabel;
        this.yearTotalMinor = yearTotalMinor;
        this.yearCurrencyCode = yearCurrencyCode == null || yearCurrencyCode.trim().isEmpty() ? "USD" : yearCurrencyCode;
        this.yearCategories = yearCategories == null ? Collections.emptyList() : yearCategories;
        this.monthItems = monthItems == null ? Collections.emptyList() : monthItems;
        this.selectedIndex = selectedIndex;
        this.selectedMonthLabel = selectedMonthLabel == null ? "" : selectedMonthLabel;
        this.selectedMonthTotalMinor = selectedMonthTotalMinor;
        this.currencyCode = currencyCode == null || currencyCode.trim().isEmpty() ? "USD" : currencyCode;
        this.selectedMonthCategories = selectedMonthCategories == null ? Collections.emptyList() : selectedMonthCategories;
        this.selectedMonthExpenses = selectedMonthExpenses == null ? Collections.emptyList() : selectedMonthExpenses;
    }

    public static PeriodScreenState empty() {
        return new PeriodScreenState(
                false,
                "",
                0L,
                "USD",
                Collections.emptyList(),
                Collections.emptyList(),
                -1,
                "",
                0L,
                "USD",
                Collections.emptyList(),
                Collections.emptyList());
    }
}
