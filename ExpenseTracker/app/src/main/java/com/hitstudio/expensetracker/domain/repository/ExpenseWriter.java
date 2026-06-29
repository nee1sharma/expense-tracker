package com.hitstudio.expensetracker.domain.repository;

import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.List;

public interface ExpenseWriter {
    long add(Expense expense);
    void update(Expense expense);
    void delete(long id);
    void addAll(List<Expense> expenses);
}
