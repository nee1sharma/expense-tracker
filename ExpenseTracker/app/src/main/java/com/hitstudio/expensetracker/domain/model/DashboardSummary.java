package com.hitstudio.expensetracker.domain.model;

import java.util.Collections;
import java.util.List;

public class DashboardSummary {
    public final Money todaySpend;
    public final Money weekSpend;
    public final Money monthSpend;
    public final List<CategoryBreakdown> topCategories;

    public DashboardSummary(Money todaySpend, Money weekSpend, Money monthSpend, List<CategoryBreakdown> topCategories) {
        this.todaySpend = todaySpend;
        this.weekSpend = weekSpend;
        this.monthSpend = monthSpend;
        this.topCategories = topCategories == null ? Collections.emptyList() : topCategories;
    }

    public static DashboardSummary empty(String currencyCode) {
        Money zero = new Money(0, currencyCode);
        return new DashboardSummary(zero, zero, zero, Collections.emptyList());
    }
}
