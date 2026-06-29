package com.hitstudio.expensetracker.data.local.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ExpenseEntity.class, CategoryEntity.class, BudgetEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ExpenseDao expenseDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();

    public static AppDatabase create(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "expenses.db").build();
    }
}
