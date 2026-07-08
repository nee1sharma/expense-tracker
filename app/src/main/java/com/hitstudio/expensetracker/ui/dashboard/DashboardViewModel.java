package com.hitstudio.expensetracker.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.DashboardSummary;
import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.List;

public class DashboardViewModel extends ViewModel {
    public final LiveData<DashboardSummary> summary;
    public final LiveData<List<Expense>> recentExpenses;
    private final AppContainer container;

    public DashboardViewModel(AppContainer container) {
        this.container = container;
        summary = container.expenseReader.observeDashboard();
        recentExpenses = container.expenseReader.observeRecent(10);
    }

    public void delete(long id) {
        container.executors.diskIO().execute(() -> container.expenseWriter.delete(id));
    }
}
