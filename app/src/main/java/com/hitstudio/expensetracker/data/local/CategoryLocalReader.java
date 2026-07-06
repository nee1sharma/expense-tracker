package com.hitstudio.expensetracker.data.local;

import androidx.lifecycle.LiveData;

import com.hitstudio.expensetracker.domain.model.Category;

import java.util.List;

public interface CategoryLocalReader {
    LiveData<List<Category>> observeAll();
    LiveData<List<Category>> observeActive();
    List<Category> getAll();
    Category getById(long id);
    Category getByName(String name);
    int count();
}
