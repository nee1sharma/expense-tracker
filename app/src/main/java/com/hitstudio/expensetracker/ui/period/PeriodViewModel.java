package com.hitstudio.expensetracker.ui.period;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.model.TransactionType;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class  PeriodViewModel extends ViewModel {
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

        YearMonth firstMonth = monthOf(expenses.get(0).occurredAt);
        YearMonth lastMonth = monthOf(expenses.get(expenses.size() - 1).occurredAt);
        List<PeriodMonthItem> monthItems = new ArrayList<>();

        YearMonth cursor = firstMonth;
        while (!cursor.isAfter(lastMonth)) {
            long startMillis = cursor.atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli();
            long endMillis = cursor.plusMonths(1).atDay(1).atStartOfDay(zoneId).toInstant().toEpochMilli();
            List<Expense> monthExpenses = filterMonth(expenses, startMillis, endMillis);
            long totalMinor = 0;
            for (Expense expense : monthExpenses) {
                if (isExpense(expense)) {
                    totalMinor += expense.amountMinor;
                }
            }
            monthItems.add(new PeriodMonthItem(
                    startMillis,
                    endMillis,
                    monthFormatter.format(cursor),
                    totalMinor,
                    defaultCurrencyCode,
                    !monthExpenses.isEmpty()
            ));
            cursor = cursor.plusMonths(1);
        }

        Long selectedStart = selectedMonthStartMillis.getValue();
        int selectedIndex = indexOf(monthItems, selectedStart);
        if (selectedIndex < 0) {
            selectedIndex = monthItems.size() - 1;
        }

        PeriodMonthItem selectedMonth = monthItems.get(selectedIndex);
        state.setValue(new PeriodScreenState(
                true,
                monthItems,
                selectedIndex,
                selectedMonth.label,
                selectedMonth.totalMinor,
                defaultCurrencyCode
        ));
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

    private List<Expense> filterMonth(List<Expense> expenses, long startMillis, long endMillis) {
        List<Expense> filtered = new ArrayList<>();
        for (Expense expense : expenses) {
            if (expense.occurredAt >= startMillis && expense.occurredAt < endMillis) {
                filtered.add(expense);
            }
        }
        filtered.sort(Comparator.comparingLong((Expense expense) -> expense.occurredAt).reversed());
        return filtered;
    }

    private YearMonth monthOf(long millis) {
        return YearMonth.from(Instant.ofEpochMilli(millis).atZone(zoneId));
    }

    private boolean isExpense(Expense expense) {
        TransactionType type = expense.transactionType == null ? TransactionType.EXPENSE : expense.transactionType;
        return type != TransactionType.INCOME;
    }
}
