package com.hitstudio.expensetracker.data.local;

import com.hitstudio.expensetracker.data.prefs.AppPreferences;
import com.hitstudio.expensetracker.domain.model.StorageMode;

public class StorageRegistry {
    private final AppPreferences preferences;
    private final ExpenseLocalReader expenseLocalReader;
    private final ExpenseLocalWriter expenseLocalWriter;
    private final CategoryLocalReader categoryLocalReader;
    private final CategoryLocalWriter categoryLocalWriter;

    public StorageRegistry(AppPreferences preferences,
                           ExpenseLocalReader expenseLocalReader,
                           ExpenseLocalWriter expenseLocalWriter,
                           CategoryLocalReader categoryLocalReader,
                           CategoryLocalWriter categoryLocalWriter) {
        this.preferences = preferences;
        this.expenseLocalReader = expenseLocalReader;
        this.expenseLocalWriter = expenseLocalWriter;
        this.categoryLocalReader = categoryLocalReader;
        this.categoryLocalWriter = categoryLocalWriter;
    }

    public StorageMode getStorageMode() {
        return preferences.getSettings().storageMode;
    }

    public ExpenseLocalReader getExpenseLocalReader() {
        return expenseLocalReader;
    }

    public ExpenseLocalWriter getExpenseLocalWriter() {
        return expenseLocalWriter;
    }

    public CategoryLocalReader getCategoryLocalReader() {
        return categoryLocalReader;
    }

    public CategoryLocalWriter getCategoryLocalWriter() {
        return categoryLocalWriter;
    }
}
