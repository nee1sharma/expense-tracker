package com.hitstudio.expensetracker.data.local.room;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "expenses",
        foreignKeys = @ForeignKey(
                entity = CategoryEntity.class,
                parentColumns = "id",
                childColumns = "categoryId",
                onDelete = ForeignKey.RESTRICT
        ),
        indices = {
                @Index("occurredAt"),
                @Index("categoryId"),
                @Index(value = {"deleted", "syncStatus"})
        }
)
public class ExpenseEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long amountMinor;
    public String currencyCode;
    public String transactionType;
    public String paymentMethod;
    public long categoryId;
    public Long accountId;
    public Long recurringRuleId;
    public String reason;
    public String payee;
    public String locationText;
    public String source;
    public long occurredAt;
    public long createdAt;
    public long updatedAt;
    public String notes;
    public String rawInput;
    public String remoteId;
    public String syncStatus;
    public boolean deleted;
}
