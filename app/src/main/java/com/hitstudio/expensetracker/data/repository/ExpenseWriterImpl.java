package com.hitstudio.expensetracker.data.repository;

import com.hitstudio.expensetracker.data.local.StorageRegistry;
import com.hitstudio.expensetracker.domain.model.Expense;
import com.hitstudio.expensetracker.domain.repository.ExpenseWriter;

import java.util.List;

public class ExpenseWriterImpl implements ExpenseWriter {
    private final StorageRegistry storageRegistry;

    public ExpenseWriterImpl(StorageRegistry storageRegistry) {
        this.storageRegistry = storageRegistry;
    }

    @Override
    public long add(Expense expense) {
        return storageRegistry.getExpenseLocalWriter().insert(expense);
    }

    @Override
    public void update(Expense expense) {
        storageRegistry.getExpenseLocalWriter().update(expense);
    }

    @Override
    public void delete(long id) {
        storageRegistry.getExpenseLocalWriter().delete(id);
    }

    @Override
    public void addAll(List<Expense> expenses) {
        storageRegistry.getExpenseLocalWriter().insertAll(expenses);
    }
}
