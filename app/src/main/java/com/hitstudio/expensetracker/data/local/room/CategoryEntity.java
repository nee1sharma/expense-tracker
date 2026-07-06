package com.hitstudio.expensetracker.data.local.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "categories", indices = {@Index(value = "name", unique = true)})
public class CategoryEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @NonNull
    public String name = "";
    public String iconName;
    public String colorHex;
    public boolean isDefault;
    public boolean isActive;
    public int sortOrder;
    public long createdAt;
    public long updatedAt;
}
