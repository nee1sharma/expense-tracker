package com.hitstudio.expensetracker.data.local.room;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.hitstudio.expensetracker.data.local.CategoryLocalReader;
import com.hitstudio.expensetracker.data.local.CategoryLocalWriter;
import com.hitstudio.expensetracker.domain.model.Category;

import java.util.List;

public class RoomCategoryLocalStore implements CategoryLocalReader, CategoryLocalWriter {
    private final CategoryDao dao;

    public RoomCategoryLocalStore(CategoryDao dao) {
        this.dao = dao;
    }

    @Override
    public LiveData<List<Category>> observeAll() {
        return Transformations.map(dao.observeAll(), CategoryEntityMapper::fromEntities);
    }

    @Override
    public LiveData<List<Category>> observeActive() {
        return Transformations.map(dao.observeActive(), CategoryEntityMapper::fromEntities);
    }

    @Override
    public List<Category> getAll() {
        return CategoryEntityMapper.fromEntities(dao.getAll());
    }

    @Override
    public Category getById(long id) {
        return CategoryEntityMapper.fromEntity(dao.getById(id));
    }

    @Override
    public Category getByName(String name) {
        return CategoryEntityMapper.fromEntity(dao.getByName(name));
    }

    @Override
    public int count() {
        return dao.count();
    }

    @Override
    public long insert(Category category) {
        long now = System.currentTimeMillis();
        if (category.createdAt <= 0) {
            category.createdAt = now;
        }
        category.updatedAt = now;
        return dao.insert(CategoryEntityMapper.toEntity(category));
    }

    @Override
    public void update(Category category) {
        category.updatedAt = System.currentTimeMillis();
        dao.update(CategoryEntityMapper.toEntity(category));
    }

    @Override
    public void hide(long id) {
        dao.hide(id, System.currentTimeMillis());
    }

    @Override
    public void insertAll(List<Category> categories) {
        dao.insertAll(CategoryEntityMapper.toEntities(categories));
    }

    @Override
    public void clearAll() {
        dao.clearAll();
    }
}
