package com.hitstudio.expensetracker.data.local;

import com.hitstudio.expensetracker.domain.model.Category;

import java.util.List;

public interface CategoryLocalWriter {
    long insert(Category category);
    void update(Category category);
    void hide(long id);
    void insertAll(List<Category> categories);
    void clearAll();
}
