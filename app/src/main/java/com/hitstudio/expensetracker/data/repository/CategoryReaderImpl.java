package com.hitstudio.expensetracker.data.repository;

import androidx.lifecycle.LiveData;

import com.hitstudio.expensetracker.data.local.StorageRegistry;
import com.hitstudio.expensetracker.domain.model.Category;
import com.hitstudio.expensetracker.domain.repository.CategoryReader;

import java.util.List;

public class CategoryReaderImpl implements CategoryReader {
    private final StorageRegistry storageRegistry;

    public CategoryReaderImpl(StorageRegistry storageRegistry) {
        this.storageRegistry = storageRegistry;
    }

    @Override
    public LiveData<List<Category>> observeAll() {
        return storageRegistry.getCategoryLocalReader().observeAll();
    }

    @Override
    public LiveData<List<Category>> observeActive() {
        return storageRegistry.getCategoryLocalReader().observeActive();
    }

    @Override
    public List<Category> getAll() {
        return storageRegistry.getCategoryLocalReader().getAll();
    }

    @Override
    public Category getById(long id) {
        return storageRegistry.getCategoryLocalReader().getById(id);
    }

    @Override
    public Category getByName(String name) {
        return storageRegistry.getCategoryLocalReader().getByName(name);
    }
}
