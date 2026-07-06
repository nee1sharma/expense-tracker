package com.hitstudio.expensetracker.data.repository;

import com.hitstudio.expensetracker.data.local.StorageRegistry;
import com.hitstudio.expensetracker.domain.model.Category;
import com.hitstudio.expensetracker.domain.repository.CategoryWriter;

import java.util.List;

public class CategoryWriterImpl implements CategoryWriter {
    private final StorageRegistry storageRegistry;

    public CategoryWriterImpl(StorageRegistry storageRegistry) {
        this.storageRegistry = storageRegistry;
    }

    @Override
    public long add(Category category) {
        return storageRegistry.getCategoryLocalWriter().insert(category);
    }

    @Override
    public void update(Category category) {
        storageRegistry.getCategoryLocalWriter().update(category);
    }

    @Override
    public void hide(long id) {
        storageRegistry.getCategoryLocalWriter().hide(id);
    }

    @Override
    public void addAll(List<Category> categories) {
        storageRegistry.getCategoryLocalWriter().insertAll(categories);
    }
}
