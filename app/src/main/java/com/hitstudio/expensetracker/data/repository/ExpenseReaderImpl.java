package com.hitstudio.expensetracker.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.hitstudio.expensetracker.data.local.StorageRegistry;
import com.hitstudio.expensetracker.data.prefs.AppPreferences;
import com.hitstudio.expensetracker.domain.model.AppSettings;
import com.hitstudio.expensetracker.domain.model.DashboardSummary;
import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.repository.ExpenseReader;
import com.hitstudio.expensetracker.domain.service.AggregationService;

import java.util.List;

public class ExpenseReaderImpl implements ExpenseReader {
    private final StorageRegistry storageRegistry;
    private final AppPreferences preferences;
    private final AggregationService aggregationService;

    public ExpenseReaderImpl(StorageRegistry storageRegistry, AppPreferences preferences, AggregationService aggregationService) {
        this.storageRegistry = storageRegistry;
        this.preferences = preferences;
        this.aggregationService = aggregationService;
    }

    @Override
    public LiveData<List<Expense>> observeRecent(int limit) {
        return storageRegistry.getExpenseLocalReader().observeRecent(limit);
    }

    @Override
    public LiveData<List<Expense>> observeAll() {
        return storageRegistry.getExpenseLocalReader().observeAll();
    }

    @Override
    public LiveData<List<Expense>> observeInRange(DateRange range) {
        return storageRegistry.getExpenseLocalReader().observeInRange(range);
    }

    @Override
    public LiveData<DashboardSummary> observeDashboard() {
        return Transformations.map(observeAll(), expenses -> {
            AppSettings settings = preferences.getSettings();
            return aggregationService.buildDashboard(
                    expenses,
                    settings.defaultCurrencyCode,
                    settings.firstDayOfWeek,
                    settings.includeIncomeOnDashboard
            );
        });
    }

    @Override
    public List<Expense> getInRange(DateRange range) {
        return storageRegistry.getExpenseLocalReader().getInRange(range);
    }

    @Override
    public List<Expense> getAll() {
        return storageRegistry.getExpenseLocalReader().getAll();
    }

    @Override
    public Expense getById(long id) {
        return storageRegistry.getExpenseLocalReader().getById(id);
    }
}
