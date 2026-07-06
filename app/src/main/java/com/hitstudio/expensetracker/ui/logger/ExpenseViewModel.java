package com.hitstudio.expensetracker.ui.logger;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.hitstudio.expensetracker.app.AppContainer;
import com.hitstudio.expensetracker.domain.model.Category;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.util.concurrent.Result;

import java.util.ArrayList;
import java.util.List;

public class ExpenseViewModel extends ViewModel {
    public final LiveData<List<Category>> categories;
    private final AppContainer container;
    private final MutableLiveData<Result<Long>> saveResult = new MutableLiveData<>();
    private final MutableLiveData<Expense> editingExpense = new MutableLiveData<>();
    private final MutableLiveData<Expense> suggestedExpense = new MutableLiveData<>();
    private final MutableLiveData<List<String>> recentReasons = new MutableLiveData<>();

    public ExpenseViewModel(AppContainer container) {
        this.container = container;
        MediatorLiveData<List<Category>> categoryLiveData = new MediatorLiveData<>();
        categoryLiveData.addSource(container.categoryReader.observeActive(), categoryLiveData::setValue);
        categories = categoryLiveData;
    }

    public LiveData<Result<Long>> getSaveResult() {
        return saveResult;
    }

    public LiveData<Expense> getEditingExpense() {
        return editingExpense;
    }

    public LiveData<Expense> getSuggestedExpense() {
        return suggestedExpense;
    }

    public LiveData<List<String>> getRecentReasons() {
        return recentReasons;
    }

    public void loadRecentReasons() {
        container.executors.diskIO().execute(() -> {
            List<String> reasons = container.expenseReader.getRecentReasons(5);
            container.executors.mainThread().execute(() -> recentReasons.setValue(reasons));
        });
    }

    public void findSuggestion(String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            return;
        }
        container.executors.diskIO().execute(() -> {
            Expense latest = container.expenseReader.getLatestByReason(reason.trim());
            if (latest != null) {
                container.executors.mainThread().execute(() -> suggestedExpense.setValue(latest));
            }
        });
    }

    public void loadExpense(long id) {
        container.executors.diskIO().execute(() -> {
            Expense expense = container.expenseReader.getById(id);
            container.executors.mainThread().execute(() -> editingExpense.setValue(expense));
        });
    }

    public void refreshCategories() {
        container.executors.diskIO().execute(() -> {
            List<Category> activeCategories = new ArrayList<>();
            for (Category category : container.categoryReader.getAll()) {
                if (category != null && category.isActive) {
                    activeCategories.add(category);
                }
            }
            container.executors.mainThread().execute(() -> {
                if (categories instanceof MutableLiveData) {
                    ((MutableLiveData<List<Category>>) categories).setValue(activeCategories);
                }
            });
        });
    }

    public void save(Expense expense) {
        String validationError = validate(expense);
        if (validationError != null) {
            saveResult.setValue(Result.error(validationError));
            return;
        }
        container.executors.diskIO().execute(() -> {
            long id = expense.id;
            if (expense.id > 0) {
                container.expenseWriter.update(expense);
            } else {
                id = container.expenseWriter.add(expense);
            }
            long savedId = id;
            container.executors.mainThread().execute(() -> saveResult.setValue(Result.success(savedId)));
        });
    }

    private String validate(Expense expense) {
        if (expense.amountMinor <= 0) {
            return "Amount must be greater than zero.";
        }
        if (expense.categoryId <= 0) {
            return "Choose a category.";
        }
        long tomorrow = System.currentTimeMillis() + 24L * 60L * 60L * 1000L;
        if (expense.occurredAt > tomorrow) {
            return "Date cannot be far in the future.";
        }
        if (tooLong(expense.reason, 120) || tooLong(expense.payee, 120) || tooLong(expense.locationText, 120)) {
            return "Short text fields must be under 120 characters.";
        }
        if (tooLong(expense.notes, 500)) {
            return "Notes must be under 500 characters.";
        }
        return null;
    }

    private boolean tooLong(String value, int max) {
        return value != null && value.length() > max;
    }
}
