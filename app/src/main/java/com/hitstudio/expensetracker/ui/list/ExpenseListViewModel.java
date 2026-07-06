package com.hitstudio.expensetracker.ui.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.List;

public class ExpenseListViewModel extends ViewModel {
    public final LiveData<List<Expense>> expenses;
    private final AppContainer container;

    public ExpenseListViewModel(AppContainer container) {
        this.container = container;
        expenses = container.expenseReader.observeAll();
    }

    public void delete(long id) {
        container.executors.diskIO().execute(() -> container.expenseWriter.delete(id));
    }
}
