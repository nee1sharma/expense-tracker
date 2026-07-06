package com.hitstudio.expensetracker.data.local.room;

import com.hitstudio.expensetracker.domain.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntityMapper {
    private CategoryEntityMapper() {
    }

    public static CategoryEntity toEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.id = category.id;
        entity.name = clean(category.name);
        entity.iconName = clean(category.iconName);
        entity.colorHex = clean(category.colorHex);
        entity.isDefault = category.isDefault;
        entity.isActive = category.isActive;
        entity.sortOrder = category.sortOrder;
        entity.createdAt = category.createdAt;
        entity.updatedAt = category.updatedAt;
        return entity;
    }

    public static Category fromEntity(CategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        Category category = new Category();
        category.id = entity.id;
        category.name = entity.name;
        category.iconName = entity.iconName;
        category.colorHex = entity.colorHex;
        category.isDefault = entity.isDefault;
        category.isActive = entity.isActive;
        category.sortOrder = entity.sortOrder;
        category.createdAt = entity.createdAt;
        category.updatedAt = entity.updatedAt;
        return category;
    }

    public static List<Category> fromEntities(List<CategoryEntity> entities) {
        List<Category> categories = new ArrayList<>();
        if (entities == null) {
            return categories;
        }
        for (CategoryEntity entity : entities) {
            categories.add(fromEntity(entity));
        }
        return categories;
    }

    public static List<CategoryEntity> toEntities(List<Category> categories) {
        List<CategoryEntity> entities = new ArrayList<>();
        if (categories == null) {
            return entities;
        }
        for (Category category : categories) {
            entities.add(toEntity(category));
        }
        return entities;
    }

    private static String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
