package com.hitstudio.expensetracker.ui.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.ui.dashboard.DashboardViewModel;
import com.hitstudio.expensetracker.ui.list.ExpenseListViewModel;
import com.hitstudio.expensetracker.ui.logger.ExpenseViewModel;
import com.hitstudio.expensetracker.ui.settings.SettingsViewModel;

public class AppViewModelFactory implements ViewModelProvider.Factory {
    private final AppContainer container;

    public AppViewModelFactory(AppContainer container) {
        this.container = container;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DashboardViewModel.class)) {
            return (T) new DashboardViewModel(container);
        }
        if (modelClass.isAssignableFrom(ExpenseViewModel.class)) {
            return (T) new ExpenseViewModel(container);
        }
        if (modelClass.isAssignableFrom(ExpenseListViewModel.class)) {
            return (T) new ExpenseListViewModel(container);
        }
        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(container);
        }
        throw new IllegalArgumentException("Unknown ViewModel: " + modelClass.getName());
    }
}
