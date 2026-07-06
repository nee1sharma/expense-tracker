package com.hitstudio.expensetracker.data.local.room;

import androidx.room.Dao;
import androidx.room.Query;

@Dao
public interface BudgetDao {
    @Query("SELECT COUNT(*) FROM budgets")
    int count();
}
