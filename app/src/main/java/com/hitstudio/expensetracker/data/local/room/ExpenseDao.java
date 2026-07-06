package com.hitstudio.expensetracker.data.local.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExpenseDao {
    @Query("SELECT e.*, c.name AS categoryName, c.colorHex AS categoryColorHex " +
            "FROM expenses e LEFT JOIN categories c ON c.id = e.categoryId " +
            "WHERE e.deleted = 0 ORDER BY e.occurredAt DESC LIMIT :limit")
    LiveData<List<ExpenseRecord>> observeRecent(int limit);

    @Query("SELECT e.*, c.name AS categoryName, c.colorHex AS categoryColorHex " +
            "FROM expenses e LEFT JOIN categories c ON c.id = e.categoryId " +
            "WHERE e.deleted = 0 ORDER BY e.occurredAt DESC")
    LiveData<List<ExpenseRecord>> observeAll();

    @Query("SELECT e.*, c.name AS categoryName, c.colorHex AS categoryColorHex " +
            "FROM expenses e LEFT JOIN categories c ON c.id = e.categoryId " +
            "WHERE e.deleted = 0 AND e.occurredAt >= :startMillis AND e.occurredAt < :endMillis " +
            "ORDER BY e.occurredAt DESC")
    LiveData<List<ExpenseRecord>> observeInRange(long startMillis, long endMillis);

    @Query("SELECT e.*, c.name AS categoryName, c.colorHex AS categoryColorHex " +
            "FROM expenses e LEFT JOIN categories c ON c.id = e.categoryId " +
            "WHERE e.deleted = 0 AND e.occurredAt >= :startMillis AND e.occurredAt < :endMillis " +
            "ORDER BY e.occurredAt DESC")
    List<ExpenseRecord> getInRange(long startMillis, long endMillis);

    @Query("SELECT e.*, c.name AS categoryName, c.colorHex AS categoryColorHex " +
            "FROM expenses e LEFT JOIN categories c ON c.id = e.categoryId " +
            "WHERE e.deleted = 0 ORDER BY e.occurredAt DESC")
    List<ExpenseRecord> getAll();

    @Query("SELECT e.*, c.name AS categoryName, c.colorHex AS categoryColorHex " +
            "FROM expenses e LEFT JOIN categories c ON c.id = e.categoryId " +
            "WHERE e.id = :id AND e.deleted = 0 LIMIT 1")
    ExpenseRecord getById(long id);

    @Query("SELECT COUNT(*) FROM expenses WHERE deleted = 0")
    int count();

    @Query("SELECT COUNT(*) FROM expenses WHERE deleted = 0 AND occurredAt >= :startMillis AND occurredAt < :endMillis")
    int countInRange(long startMillis, long endMillis);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ExpenseEntity entity);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertAll(List<ExpenseEntity> entities);

    @Update
    void update(ExpenseEntity entity);

    @Query("UPDATE expenses SET deleted = 1, updatedAt = :updatedAt WHERE id = :id")
    void softDelete(long id, long updatedAt);

    @Query("DELETE FROM expenses")
    void clearAll();
}
