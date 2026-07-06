package com.hitstudio.expensetracker.data.local.room;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "budgets",
        foreignKeys = @ForeignKey(
                entity = CategoryEntity.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = {@Index("categoryId")}
)
public class BudgetEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public Long categoryId;
    public long amountMinor;
    public String currencyCode;
    public String period;
    public int alertThresholdPercent;
    public boolean isActive;
    public long createdAt;
    public long updatedAt;
}
