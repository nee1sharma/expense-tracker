package com.hitstudio.expensetracker.ui.list;

import androidx.lifecycle.ViewModel;

import com.hitstudio.expensetracker.app.AppContainer;

public class ExpenseListViewModel extends ViewModel {
    private final AppContainer container;

    public ExpenseListViewModel(AppContainer container) {
        this.container = container;
    }

    public void delete(long id) {
        container.executors.diskIO().execute(() -> container.expenseWriter.delete(id));
    }
}
