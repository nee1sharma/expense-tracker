package com.hitstudio.expensetracker.data.local;

import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.List;

public interface ExpenseLocalWriter {
    long insert(Expense expense);
    void update(Expense expense);
    void delete(long id);
    void insertAll(List<Expense> expenses);
    void clearAll();
}
