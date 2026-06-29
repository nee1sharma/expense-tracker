package com.hitstudio.expensetracker.domain.repository;

import androidx.lifecycle.LiveData;

import com.hitstudio.expensetracker.domain.model.DashboardSummary;
import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.List;

public interface ExpenseReader {
    LiveData<List<Expense>> observeRecent(int limit);
    LiveData<List<Expense>> observeAll();
    LiveData<List<Expense>> observeInRange(DateRange range);
    LiveData<DashboardSummary> observeDashboard();
    List<Expense> getInRange(DateRange range);
    List<Expense> getAll();
    Expense getById(long id);
}
