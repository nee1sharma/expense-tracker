package com.hitstudio.expensetracker.data.local;

import androidx.lifecycle.LiveData;

import com.hitstudio.expensetracker.domain.model.DateRange;
import com.hitstudio.expensetracker.domain.model.Expense;

import java.util.List;

public interface ExpenseLocalReader {
    LiveData<List<Expense>> observeInRange(DateRange range);
    LiveData<List<Expense>> observeRecent(int limit);
    LiveData<List<Expense>> observeAll();
    List<Expense> getInRange(DateRange range);
    List<Expense> getAll();
    Expense getById(long id);
    Expense getLatestByReason(String reason);
    List<String> getRecentReasons(int limit);
    int count();
}
