package com.hitstudio.expensetracker.domain.repository;

import com.hitstudio.expensetracker.domain.model.Category;

import java.util.List;

public interface CategoryWriter {
    long add(Category category);
    void update(Category category);
    void hide(long id);
    void addAll(List<Category> categories);
}
