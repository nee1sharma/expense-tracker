package com.hitstudio.expensetracker.domain.model;

public class Category {
    public long id;
    public String name;
    public String iconName;
    public String colorHex;
    public boolean isDefault;
    public boolean isActive;
    public int sortOrder;
    public long createdAt;
    public long updatedAt;

    public Category() {
    }

    public Category(String name, String iconName, String colorHex, boolean isDefault, boolean isActive, int sortOrder) {
        long now = System.currentTimeMillis();
        this.name = name;
        this.iconName = iconName;
        this.colorHex = colorHex;
        this.isDefault = isDefault;
        this.isActive = isActive;
        this.sortOrder = sortOrder;
        this.createdAt = now;
        this.updatedAt = now;
    }
}
