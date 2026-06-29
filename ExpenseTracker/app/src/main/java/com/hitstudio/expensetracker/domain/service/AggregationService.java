package com.hitstudio.expensetracker.domain.service;

import com.hitstudio.expensetracker.domain.model.CategoryBreakdown;
import com.hitstudio.expensetracker.domain.model.DashboardSummary;
import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.model.Money;
import com.hitstudio.expensetracker.domain.model.TransactionType;
import com.hitstudio.expensetracker.util.PeriodHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationService {
    public DashboardSummary buildDashboard(List<Expense> expenses, String currencyCode,
                                           int firstDayOfWeek, boolean includeIncomeInNet) {
        if (expenses == null || expenses.isEmpty()) {
            return DashboardSummary.empty(currencyCode);
        }
        long now = System.currentTimeMillis();
        DateRange today = PeriodHelper.today(now);
        DateRange week = PeriodHelper.thisWeek(now, firstDayOfWeek);
        DateRange month = PeriodHelper.thisMonth(now);

        long todayTotal = 0;
        long weekTotal = 0;
        long monthTotal = 0;
        Map<Long, CategoryAccumulator> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {
            long signed = signedAmount(expense, includeIncomeInNet);
            if (today.contains(expense.occurredAt)) {
                todayTotal += signed;
            }
            if (week.contains(expense.occurredAt)) {
                weekTotal += signed;
            }
            if (month.contains(expense.occurredAt)) {
                monthTotal += signed;
                if (expense.transactionType == TransactionType.EXPENSE) {
                    CategoryAccumulator accumulator = categoryTotals.get(expense.categoryId);
                    if (accumulator == null) {
                        accumulator = new CategoryAccumulator(expense.categoryId, expense.categoryName, expense.categoryColorHex);
                        categoryTotals.put(expense.categoryId, accumulator);
                    }
                    accumulator.totalMinor += expense.amountMinor;
                }
            }
        }

        List<CategoryBreakdown> breakdowns = new ArrayList<>();
        for (CategoryAccumulator accumulator : categoryTotals.values()) {
            breakdowns.add(new CategoryBreakdown(
                    accumulator.categoryId,
                    accumulator.categoryName,
                    accumulator.colorHex,
                    new Money(accumulator.totalMinor, currencyCode)
            ));
        }
        Collections.sort(breakdowns, (left, right) -> Long.compare(right.total.amountMinor, left.total.amountMinor));
        if (breakdowns.size() > 5) {
            breakdowns = new ArrayList<>(breakdowns.subList(0, 5));
        }

        return new DashboardSummary(
                new Money(todayTotal, currencyCode),
                new Money(weekTotal, currencyCode),
                new Money(monthTotal, currencyCode),
                breakdowns
        );
    }

    private long signedAmount(Expense expense, boolean includeIncomeInNet) {
        if (expense.transactionType == TransactionType.INCOME) {
            return includeIncomeInNet ? -expense.amountMinor : 0;
        }
        return expense.amountMinor;
    }

    private static class CategoryAccumulator {
        final long categoryId;
        final String categoryName;
        final String colorHex;
        long totalMinor;

        CategoryAccumulator(long categoryId, String categoryName, String colorHex) {
            this.categoryId = categoryId;
            this.categoryName = categoryName == null ? "Uncategorized" : categoryName;
            this.colorHex = colorHex == null ? "#607D8B" : colorHex;
        }
    }
}
