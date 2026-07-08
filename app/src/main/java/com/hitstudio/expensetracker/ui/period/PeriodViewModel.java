package com.hitstudio.expensetracker.ui.period;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.model.TransactionType;

import java.time.Instant;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PeriodViewModel extends ViewModel {
    private final LiveData<List<Expense>> allExpenses;
    private final MutableLiveData<Long> selectedMonthStartMillis = new MutableLiveData<>();
    private final MediatorLiveData<PeriodScreenState> state = new MediatorLiveData<>();
    private final ZoneId zoneId = ZoneId.systemDefault();
    private final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.getDefault());
    private final String defaultCurrencyCode;
    private List<Expense> currentExpenses = Collections.emptyList();

    public PeriodViewModel(AppContainer container) {
        this.allExpenses = container.expenseReader.observeAll();
        this.defaultCurrencyCode = container.preferences.getSettings().defaultCurrencyCode;

        state.addSource(allExpenses, expenses -> {
            currentExpenses = expenses == null ? Collections.emptyList() : new ArrayList<>(expenses);
            rebuildState();
        });
        state.addSource(selectedMonthStartMillis, ignored -> rebuildState());
        rebuildState();
    }

    public LiveData<PeriodScreenState> getState() {
        return state;
    }

    public void selectMonth(long startMillis) {
        selectedMonthStartMillis.setValue(startMillis);
    }

    private void rebuildState() {
        if (currentExpenses == null || currentExpenses.isEmpty()) {
            state.setValue(PeriodScreenState.empty());
            return;
        }

        List<Expense> expenses = new ArrayList<>(currentExpenses);
        expenses.sort(Comparator.comparingLong(expense -> expense.occurredAt));

        Year currentYear = Year.now(zoneId);
        List<Expense> yearExpenses = filterRange(
                expenses,
                currentYear.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli(),
                currentYear.plusYears(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli());
        String yearCurrencyCode = currencyFor(yearExpenses);

        YearMonth firstMonth = monthOf(expenses.get(0).occurredAt);
        YearMonth currentMonth = YearMonth.now(zoneId);
        YearMonth newestMonth = currentMonth.isBefore(firstMonth) ? firstMonth : currentMonth;
        List<PeriodMonthItem> monthItems = buildMonthItems(expenses, newestMonth, firstMonth);

        int selectedIndex = indexOf(monthItems, selectedMonthStartMillis.getValue());
        if (selectedIndex < 0) {
            state.setValue(new PeriodScreenState(
                    true,
                    String.valueOf(currentYear.getValue()),
                    totalExpenseMinor(yearExpenses),
                    yearCurrencyCode,
                    buildCategoryItems(yearExpenses, yearCurrencyCode),
                    monthItems,
                    -1,
                    "",
                    0L,
                    safeCurrency(defaultCurrencyCode),
                    Collections.emptyList(),
                    Collections.emptyList()
            ));
            return;
        }

        PeriodMonthItem selectedMonth = monthItems.get(selectedIndex);
        List<Expense> monthExpenses = filterRange(expenses, selectedMonth.startMillis, selectedMonth.endMillis);
        String monthCurrencyCode = currencyFor(monthExpenses);
        state.setValue(new PeriodScreenState(
                true,
                String.valueOf(currentYear.getValue()),
                totalExpenseMinor(yearExpenses),
                yearCurrencyCode,
                buildCategoryItems(yearExpenses, yearCurrencyCode),
                monthItems,
                selectedIndex,
                selectedMonth.label,
                selectedMonth.totalMinor,
                monthCurrencyCode,
                buildCategoryItems(monthExpenses, monthCurrencyCode),
                monthExpenses
        ));
    }

    private List<PeriodMonthItem> buildMonthItems(List<Expense> expenses, YearMonth newestMonth, YearMonth oldestMonth) {
        List<PeriodMonthItem> monthItems = new ArrayList<>();
        YearMonth cursor = newestMonth;
        while (!cursor.isBefore(oldestMonth)) {
            long startMillis = cursor.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli();
            long endMillis = cursor.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli();
            List<Expense> monthExpenses = filterRange(expenses, startMillis, endMillis);
            String currencyCode = currencyFor(monthExpenses);
            monthItems.add(new PeriodMonthItem(
                    startMillis,
                    endMillis,
                    monthFormatter.format(cursor),
                    totalExpenseMinor(monthExpenses),
                    currencyCode,
                    hasExpenseTransactions(monthExpenses)
            ));
            cursor = cursor.minusMonths(1);
        }
        return monthItems;
    }

    private int indexOf(List<PeriodMonthItem> monthItems, Long selectedStart) {
        if (selectedStart == null) {
            return -1;
        }
        for (int i = 0; i < monthItems.size(); i++) {
            if (monthItems.get(i).startMillis == selectedStart) {
                return i;
            }
        }
        return -1;
    }

    private List<Expense> filterRange(List<Expense> expenses, long startMillis, long endMillis) {
        List<Expense> filtered = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.occurredAt >= startMillis && expense.occurredAt < endMillis) {
                filtered.add(expense);
            }
        }
        filtered.sort(Comparator.comparingLong((Expense expense) -> expense.occurredAt).reversed());
        return filtered;
    }

    private List<PeriodCategoryItem> buildCategoryItems(List<Expense> expenses, String currencyCode) {
        Map<Long, CategoryAccumulator> buckets = new LinkedHashMap<>();
        for (Expense expense : expenses) {
            if (!isExpense(expense)) {
                continue;
            }
            CategoryAccumulator accumulator = buckets.get(expense.categoryId);
            if (accumulator == null) {
                accumulator = new CategoryAccumulator(
                        expense.categoryId,
                        emptyToFallback(expense.categoryName, "Uncategorized"),
                        emptyToFallback(expense.categoryColorHex, "#607D8B")
                );
                buckets.put(expense.categoryId, accumulator);
            }
            accumulator.totalMinor += expense.amountMinor;
        }

        List<PeriodCategoryItem> items = new ArrayList<>();
        for (CategoryAccumulator accumulator : buckets.values()) {
            items.add(new PeriodCategoryItem(
                    accumulator.categoryId,
                    accumulator.categoryName,
                    accumulator.colorHex,
                    accumulator.totalMinor,
                    currencyCode
            ));
        }
        items.sort((left, right) -> Long.compare(right.amountMinor, left.amountMinor));
        return items;
    }

    private long totalExpenseMinor(List<Expense> expenses) {
        long totalMinor = 0L;
        for (Expense expense : expenses) {
            if (isExpense(expense)) {
                totalMinor += expense.amountMinor;
            }
        }
        return totalMinor;
    }

    private boolean hasExpenseTransactions(List<Expense> expenses) {
        for (Expense expense : expenses) {
            if (isExpense(expense)) {
                return true;
            }
        }
        return false;
    }

    private YearMonth monthOf(long millis) {
        return YearMonth.from(Instant.ofEpochMilli(millis).atZone(zoneId));
    }

    private String currencyFor(List<Expense> expenses) {
        for (Expense expense : expenses) {
            if (expense.currencyCode != null && !expense.currencyCode.trim().isEmpty()) {
                return expense.currencyCode;
            }
        }
        return safeCurrency(defaultCurrencyCode);
    }

    private String safeCurrency(String currencyCode) {
        return currencyCode == null || currencyCode.trim().isEmpty() ? "USD" : currencyCode;
    }

    private String emptyToFallback(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    private boolean isExpense(Expense expense) {
        TransactionType type = expense.transactionType == null ? TransactionType.EXPENSE : expense.transactionType;
        return type != TransactionType.INCOME;
    }

    private static final class CategoryAccumulator {
        final long categoryId;
        final String categoryName;
        final String colorHex;
        long totalMinor;

        CategoryAccumulator(long categoryId, String categoryName, String colorHex) {
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.colorHex = colorHex;
        }
    }
}
