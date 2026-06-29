package com.hitstudio.expensetracker.data.local.room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    LiveData<List<CategoryEntity>> observeAll();

    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY sortOrder ASC, name ASC")
    LiveData<List<CategoryEntity>> observeActive();

    @Query("SELECT * FROM categories ORDER BY sortOrder ASC, name ASC")
    List<CategoryEntity> getAll();

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    CategoryEntity getById(long id);

    @Query("SELECT * FROM categories WHERE name = :name COLLATE NOCASE LIMIT 1")
    CategoryEntity getByName(String name);

    @Query("SELECT COUNT(*) FROM categories")
    int count();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(CategoryEntity entity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAll(List<CategoryEntity> entities);

    @Update
    void update(CategoryEntity entity);

    @Query("UPDATE categories SET isActive = 0, updatedAt = :updatedAt WHERE id = :id")
    void hide(long id, long updatedAt);

    @Query("DELETE FROM categories")
    void clearAll();
}
